# sast2023 Android-App: Peg Solitaire

**目录**
- [sast2023 Android-App: Peg Solitaire](#sast2023-android-app-peg-solitaire)
  - [简介](#简介)
  - [运行方法](#运行方法)
  - [游戏规则](#游戏规则)
  - [游戏功能](#游戏功能)
    - [选择棋盘样式](#选择棋盘样式)
    - [查看名人堂](#查看名人堂)
    - [更换图标](#更换图标)
  - [发行](#发行)

## 简介

本仓库是 2023 酒井科协暑培 Java & Android 的课程作业。

> 单人跳棋是一种单人智力挑战的游戏，需要玩家预测和计划跳跃的顺序，以便最大限度地减少剩余棋子的数量。
> 由于棋子的移动和跳跃规则相对简单，但游戏的难度可以通过调整初始布局和规则来增加。这使得单人跳棋成为一种有趣而富有挑战性的解谜游戏。

## 运行方法

使用 [Android Studio Giraffe](https://developer.android.google.cn/studio) 进行构建与运行。

也可以通过在 [GitHub Release](https://github.com/LeverImmy/sast2023-java-and-android/releases) 界面直接下载 apk 安装包。

还可以通过在 [清华云盘](https://cloud.tsinghua.edu.cn/d/9319c708d1674091af93/) 界面直接下载 apk 安装包。

## 游戏规则

下面是单人跳棋的基本规则：

1. 棋盘：
  单人跳棋使用的棋盘上有一系列交叉的线，形成许多交叉点。
2. 棋子放置：
  初始时，棋盘上除一个交叉点外，其他交叉点上都会放置一个棋子。
3. 移动规则：
  玩家可以选择一个棋子进行移动。棋子可以沿着棋盘上的线水平或垂直方向移动，但不能斜向移动。
4. 跳跃规则：
  玩家可以用一个棋子（记为 A）跳过 **相邻的** 另一个棋子（记为 B），到达该方向的**与 B 相邻的下一个格子**，作为“落点”。操作棋子、被跳过的棋子和落点必须是一条直线，**且落点是一个空位**。被跳过的棋子 B 将被移除。如果在跳跃后，仍然存在可以跳过的棋子，玩家可以选择继续跳跃。**这样的连续跳跃可以一直进行下去**，直到没有可跳过的棋子为止。 **这样的一系列操作算一步。**
5. 目标：
  玩家的目标是通过一系列的跳跃，最终只剩下一个棋子在棋盘上。（最佳的结果是剩下中心位置的那个棋子。）

更多详细信息请移步 [Peg solitaire - Wikipedia](https://en.wikipedia.org/wiki/Peg_solitaire)。

## 游戏功能

### 选择棋盘样式

在游戏中，支持 **更换多种棋盘样式**。

同时，在构建之前，支持在 `./app/src/main/assets/boards.json` 里导入棋盘样式。例如

```json
{
  "boards": [
    {
      "name": "English Style",
      "map": [
        [1, 1, 3, 3, 3, 1, 1],
        [1, 1, 3, 3, 3, 1, 1],
        [3, 3, 3, 3, 3, 3, 3],
        [3, 3, 3, 2, 3, 3, 3],
        [3, 3, 3, 3, 3, 3, 3],
        [1, 1, 3, 3, 3, 1, 1],
        [1, 1, 3, 3, 3, 1, 1]
      ]
    },
    {
      "name": "Easy",
      "map": [
        [2, 1, 1],
        [3, 1, 1],
        [2, 3, 3]
      ]
    }
  ]
}
```

其中 `1` 表示占位符，`2` 表示空格，`3` 表示棋子。

### 查看名人堂

对于每个棋盘样式，支持存储并查看目前为止，完成游戏的 **最佳步数** 和 **完成者姓名**。

### 更换图标

在构建之前，支持在 `./app/src/main/res/drawable/` 下更换棋子、空格、提示语的图标。

## 发行

单人跳棋 v1.0.1 已发行。

支持 [GitHub Release](https://github.com/LeverImmy/sast2023-java-and-android/releases) 和 [清华云盘](https://cloud.tsinghua.edu.cn/d/9319c708d1674091af93/) 两种方式直接下载 apk 安装包。
