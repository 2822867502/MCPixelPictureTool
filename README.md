# MCPixelPictureTool

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

一个基于Lab色彩空间的高质量Minecraft像素画生成工具，支持上百种游戏内方块。

## 项目特点 ✨

- **高级色彩匹配**：使用Lab色彩空间进行方块选择，更贴近人眼感知的色差计算
- **多用处支持**：
  - Minecraft原版结构方块
  - 机械动力 (Create Mod) 蓝图
  - Litematica投影模组
- **丰富方块库**：支持上百种Minecraft方块及其变种
- **智能优化**：自动选择最佳匹配方块组合

## 即将实现的功能 🚧

- **更多输出格式**：
  - 投影模组 (Litematica) 格式的完整支持
  - 命令方块序列生成
- **更多的方块支持**
  - 支持自导入MOD方块
  - 更简单的颜色获取工具
- **性能优化**：
  - 多线程处理加速生成
  - 内存使用优化
- **高级生成模式**：
  - 3D体素模型生成
  - 视频转像素动画
- **多语言界面**：
  - 国际化支持

## 安装与使用

### 系统要求
- Java 8 或更高版本

### 快速开始
1. 从 [Releases 页面](https://github.com/yourusername/MCPixelPictureTool/releases) 下载最新版本
2. 运行应用程序：
   ```bash
   java -jar MCPixelPictureTool.jar
   ```
3. 根据GUI界面填写参数并使用

## 依赖项

本项目基于以下优秀开源库构建：
- [DarkLaf](https://github.com/weisJ/darklaf) - 现代跨平台外观
- [NBT](https://github.com/Querz/NBT) - NBT格式处理库

## 贡献指南

欢迎贡献！请遵循以下步骤：

1. Fork 本项目仓库
2. 创建新分支 (`git checkout -b feature/your-feature`)
3. 提交更改 (`git commit -am 'Add some feature'`)
4. 推送到分支 (`git push origin feature/your-feature`)
5. 创建Pull Request

## 联系方式

- **邮箱**: zlk2822867502@163.com
- **B站主页**: [https://space.bilibili.com/609925863](https://space.bilibili.com/609925863)
- **问题追踪**: [GitHub Issues](https://github.com/yourusername/MCPixelPictureTool/issues)

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 开源协议
