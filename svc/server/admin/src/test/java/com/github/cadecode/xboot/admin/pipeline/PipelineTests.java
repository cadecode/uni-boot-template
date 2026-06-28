package com.github.cadecode.xboot.admin.pipeline;

import com.github.cadecode.xboot.common.exception.ExtensionException;
import com.github.cadecode.xboot.common.extension.pipeline.PipelineExecutor;
import com.github.cadecode.xboot.common.extension.pipeline.selector.FilterSelectorFactory;
import com.github.cadecode.xboot.common.extension.pipeline.selector.DummyFilterSelector;
import com.github.cadecode.xboot.common.extension.pipeline.selector.FilterSelector;
import com.github.cadecode.xboot.common.extension.pipeline.selector.LocalListFilterSelector;
import com.github.cadecode.xboot.common.extension.pipeline.selector.MatchAllFilterSelector;
import com.github.cadecode.xboot.common.extension.pipeline.selector.YmlConfigFilterSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "x-boot.extension.pipeline.filter-selectors.BIZ1=PipelineTestFilterA,PipelineTestFilterB,PipelineTestFilterC",
        "x-boot.extension.pipeline.filter-selectors.BIZ2=PipelineTestFilterB,PipelineTestFilterC",
        "x-boot.extension.pipeline.filter-selectors.BIZ3=PipelineTestFilterA",
        "x-boot.extension.pipeline.filter-selectors.BIZ4=on",
        "x-boot.extension.pipeline.filter-selectors.BIZ5=off",
})
public class PipelineTests {

    @Autowired
    private PipelineTestFilterA filterA;

    @Autowired
    private PipelineTestFilterB filterB;

    @Autowired
    private PipelineTestFilterC filterC;

    @Autowired
    private FilterSelectorFactory selectorFactory;

    private PipelineExecutor<PipelineTestContext> executor;

    @BeforeEach
    void setUp() {
        executor = new PipelineExecutor<>();
        executor.appendFilter(filterA, "PipelineTestFilterA");
        executor.appendFilter(filterB, "PipelineTestFilterB");
        executor.appendFilter(filterC, "PipelineTestFilterC");
    }

    @Test
    @DisplayName("编程式：手工 appendFilter + LocalListFilterSelector")
    void testProgrammatic_basic() {
        LocalListFilterSelector selector = new LocalListFilterSelector(List.of(
                "PipelineTestFilterA", "PipelineTestFilterB", "PipelineTestFilterC"));
        executor.execute(new PipelineTestContext(PipelineTestType.BIZ1, selector));
    }

    @Test
    @DisplayName("编程式：只启用 PipelineTestFilterA")
    void testProgrammatic_selective() {
        LocalListFilterSelector selector = new LocalListFilterSelector(List.of("PipelineTestFilterA"));
        executor.execute(new PipelineTestContext(PipelineTestType.BIZ1, selector));
    }

    @Test
    @DisplayName("编程式：空选择器")
    void testProgrammatic_empty() {
        executor.execute(new PipelineTestContext(PipelineTestType.BIZ1, new LocalListFilterSelector(List.of())));
    }

    @Test
    @DisplayName("API：空 chain 执行时抛 ExtensionException")
    void testApiCompatibility_emptyChain() {
        PipelineExecutor<PipelineTestContext> empty = new PipelineExecutor<>();
        assertNull(empty.getFirstChain());
        assertThrows(ExtensionException.class,
                () -> empty.execute(new PipelineTestContext(PipelineTestType.BIZ1, new MatchAllFilterSelector())));
    }

    @Test
    @DisplayName("API 兼容：appendFilter 后 firstChain 非 null")
    void testApiCompatibility_appendFilter() {
        PipelineExecutor<PipelineTestContext> gen = new PipelineExecutor<>();
        gen.appendFilter(filterA, "A");
        assertNotNull(gen.getFirstChain());
    }

    @Test
    @DisplayName("声明式：BIZ1 启用全部 3 个 filter")
    void testDeclarative_biz1All() {
        FilterSelector selector = selectorFactory.createFilterSelector(PipelineTestType.BIZ1);
        assertTrue(selector instanceof YmlConfigFilterSelector);
        assertTrue(selector.matchFilter("PipelineTestFilterA"));
        assertTrue(selector.matchFilter("PipelineTestFilterB"));
        assertTrue(selector.matchFilter("PipelineTestFilterC"));
        executor.execute(new PipelineTestContext(PipelineTestType.BIZ1, selector));
    }

    @Test
    @DisplayName("声明式：BIZ2 只启用 B/C")
    void testDeclarative_biz2Partial() {
        FilterSelector selector = selectorFactory.createFilterSelector(PipelineTestType.BIZ2);
        assertTrue(selector instanceof YmlConfigFilterSelector);
        assertFalse(selector.matchFilter("PipelineTestFilterA"));
        assertTrue(selector.matchFilter("PipelineTestFilterB"));
        assertTrue(selector.matchFilter("PipelineTestFilterC"));
        executor.execute(new PipelineTestContext(PipelineTestType.BIZ2, selector));
    }

    @Test
    @DisplayName("声明式：BIZ3 只启用 A")
    void testDeclarative_biz3Partial() {
        FilterSelector selector = selectorFactory.createFilterSelector(PipelineTestType.BIZ3);
        assertTrue(selector instanceof YmlConfigFilterSelector);
        assertTrue(selector.matchFilter("PipelineTestFilterA"));
        assertFalse(selector.matchFilter("PipelineTestFilterB"));
        assertFalse(selector.matchFilter("PipelineTestFilterC"));
        executor.execute(new PipelineTestContext(PipelineTestType.BIZ3, selector));
    }

    @Test
    @DisplayName("声明式：type=on → MatchAllFilterSelector")
    void testDeclarative_on() {
        FilterSelector selector = selectorFactory.createFilterSelector(PipelineTestType.BIZ4);
        assertTrue(selector instanceof MatchAllFilterSelector);
        executor.execute(new PipelineTestContext(PipelineTestType.BIZ4, selector));
    }

    @Test
    @DisplayName("声明式：type=off → DummyFilterSelector")
    void testDeclarative_off() {
        FilterSelector selector = selectorFactory.createFilterSelector(PipelineTestType.BIZ5);
        assertTrue(selector instanceof DummyFilterSelector);
        executor.execute(new PipelineTestContext(PipelineTestType.BIZ5, selector));
    }

    @Test
    @DisplayName("声明式：未配置 type → 抛出 ExtensionException")
    void testDeclarative_unconfigured() {
        assertThrows(ExtensionException.class,
                () -> selectorFactory.createFilterSelector(PipelineTestType.UNKNOWN));
    }

    @Test
    void testMatchAllFilterSelector() {
        FilterSelector selector = new MatchAllFilterSelector();
        assertTrue(selector.matchFilter("Anything"));
        assertEquals(Collections.emptyList(), selector.getFilterNames());
    }

    @Test
    void testDummyFilterSelector() {
        FilterSelector selector = new DummyFilterSelector();
        assertFalse(selector.matchFilter("Anything"));
        assertEquals(Collections.emptyList(), selector.getFilterNames());
    }

    @Test
    void testLocalListFilterSelector() {
        LocalListFilterSelector selector = new LocalListFilterSelector(List.of("PipelineTestFilterA", "PipelineTestFilterC"));
        assertTrue(selector.matchFilter("PipelineTestFilterA"));
        assertFalse(selector.matchFilter("PipelineTestFilterB"));
        assertTrue(selector.matchFilter("PipelineTestFilterC"));
    }

    @Test
    void testYmlConfigFilterSelector_fromFactory() {
        FilterSelector selector = selectorFactory.createFilterSelector(PipelineTestType.BIZ1);
        assertTrue(selector instanceof YmlConfigFilterSelector);
        assertEquals(List.of("PipelineTestFilterA", "PipelineTestFilterB", "PipelineTestFilterC"), selector.getFilterNames());
    }
}
