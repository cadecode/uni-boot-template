# 仓库拆分指南

> 将当前 x-boot-template（monorepo, svc/ + app/）拆分为前后端两个独立仓库。

---

## 前置条件

- GitHub 空仓库：`x-boot-template-backend`、`x-boot-template-frontend`
- 安装 git-filter-repo（非内置命令，需单独装：`brew install git-filter-repo` / `scoop install git-filter-repo` / `pip install git-filter-repo`）

## 步骤

### 1. 拆分后端

```bash
cd ~/projects
git clone x-boot-template x-boot-template-backend
cd x-boot-template-backend
git filter-repo --path svc/ --path-rename svc/:
# 注意：filter-repo 只修改 clone 副本，不影响原仓库；会自动清除旧 origin
git remote add origin git@github.com:团队/x-boot-template-backend.git
git push -u origin --all
# 结果：common/ server/ framework/ starter/ pom.xml .gitignore ...
```

### 2. 拆分前端

```bash
cd ~/projects
git clone x-boot-template x-boot-template-frontend
cd x-boot-template-frontend
git filter-repo --path app/ --path-rename app/:
git remote add origin git@github.com:团队/x-boot-template-frontend.git
git push -u origin --all
# 结果：src/ package.json .gitignore ...
```

### 3. 处理原仓库（可选）

```bash
cd ~/projects/x-boot-template
git remote rename origin origin-archive
# 原仓库不受 filter-repo 影响，可继续使用或归档
```

## 参数说明

- `--path svc/`：只保留 `svc/` 下的文件，其余全部丢弃
- `--path-rename svc/:`：去掉 `svc/` 前缀（`svc/common/` → `common/`）。忘记此参数则文件留在 `svc/` 子目录下

## 效果

filter-repo 重写 git 历史，每个新仓库只包含与自己相关的 commit。原仓库的 `.trellis/` 不在保留路径内，不会进入新仓库。

## 替代方案：手动复制（不保留历史）

无需安装，丢失 git 历史。

```bash
mkdir x-boot-template-backend && cd "$_" && git init
cp -r ../x-boot-template/svc/* .
git add . && git commit -m "init: 拆分后端"
git remote add origin <remote> && git push -u origin --all
```
