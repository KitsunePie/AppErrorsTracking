# AppErrorsTracking

[![GitHub license](https://img.shields.io/github/license/KitsunePie/AppErrorsTracking?color=blue&style=flat-square)](https://github.com/KitsunePie/AppErrorsTracking/blob/master/LICENSE)
[![GitHub CI](https://img.shields.io/github/actions/workflow/status/KitsunePie/AppErrorsTracking/commit_ci.yml?label=CI%20builds&style=flat-square)](https://github.com/KitsunePie/AppErrorsTracking/actions/workflows/commit_ci.yml)
[![GitHub release](https://img.shields.io/github/v/release/KitsunePie/AppErrorsTracking?display_name=release&logo=github&color=green&style=flat-square)](https://github.com/KitsunePie/AppErrorsTracking/releases)
![GitHub all releases](https://img.shields.io/github/downloads/KitsunePie/AppErrorsTracking/total?label=downloads&style=flat-square)
![GitHub all releases](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.fankes.apperrorstracking/total?label=LSPosed%20downloads&labelColor=F48FB1&style=flat-square)

[![Telegram CI](https://img.shields.io/badge/CI%20builds-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/AppErrorsTracking_CI)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/XiaofangInternet)
[![QQ](https://img.shields.io/badge/discussion-QQ-blue.svg?logo=tencent-qq&logoColor=red&style=flat-square)](https://qm.qq.com/cgi-bin/qm/qr?k=dp2h5YhWiga9WWb_Oh7kSHmx01X8I8ii&jump_from=webapi&authKey=Za5CaFP0lk7+Zgsk2KpoBD7sSaYbeXbsDgFjiWelOeH4VSionpxFJ7V0qQBSqvFM)
[![QQ 频道](https://img.shields.io/badge/discussion-QQ%20频道-blue.svg?logo=tencent-qq&logoColor=red&style=flat-square)](https://pd.qq.com/s/44gcy28h)

<img src="img-src/icon.png" width = "100" height = "100" alt="LOGO"/>

[English](README.md) | [简体中文](README-zh-CN.md) | 日本語

アプリのエラーダイアログに機能を追加し、カスタムROMによって削除されたダイアログを修正することで、Android開発者に最高の体験を提供します。

このプロジェクトは、どのAndroidシステム上でも使用できるXposedモジュールであり、現在は**LSPosed**でのみテストされています。

このXposedモジュールは、Android開発者のために特別に設計されています。

PCに接続できない、ADBが実行できない状態である場合にこのモジュールを使用して、インストールされているアプリのエラーをキャプチャする事で問題を迅速に特定することができます。

アプリがクラッシュしたときのエラーログは、開発者にとって貴重な財産です。もしあなたが開発者でなくても、このモジュールをインストールする事で開発者への貢献に繋がるでしょう。

> 最小サポート Android 7.0

## プロジェクトの理由

本当に理解不能ですが、中国本土のAndroid ROMは、MIUI(安定版を除く)を除いて、アプリのクラッシュ時のダイアログボックス(強制終了ダイアログ)を削除しています。私はシステムフレームワークを逆コンパイルして本当に削除されていることを確認するまで、これは当たり前の機能だと思っていました。

プロダクトマネージャーは、ユーザーにエラーを表示させずにアプリをクラッシュさせて直接終了する事が最善の解決策と考えているのでしょうか?
それとも **隠された秘密** があるのでしょうか?

## 動作の原理

`Thread.UncaughtExceptionHandler`とは異なり、システムフレームワークをインジェクトする事でアプリのエラーを全方向からキャプチャするネイティブメソッドを使用します。これは、元の例外監視よりもパフォーマンスに優れています。

同時に、システムレベルの例外のキャプチャは、ネイティブプラットフォームの`スタックトレース`もキャプチャ可能です。

## 注意事項

システムによってネイティブにキャプチャされるエラーは、アプリ自体によって処理されないエラーのみです。アプリ自体に**Bugly**のような、エラーを自動的に収集するためのカスタムの `Thread.UncaughtExceptionHandler` がある場合、システムはアプリが実際にクラッシュ **(強制終了)** したかどうかを取得できません。

## 機能のリスト

- [x] システムのアプリエラーダイアログを完全に置き換え

- [x] 各アプリの例外をログに記録して再起動まで保持

- [x] エラーのスタックトレース関数のコピー、共有、エクスポート

- [x] エラー履歴記録機能、これは通知バータイルの「エラー履歴の記録」およびモジュールのメインインターフェースから入力

- [x] アプリのエラー統計機能

- [x] マルチプロセスアプリのエラー表示機能

## 翻訳の貢献

このプロジェクトは、あなたの国の言語に翻訳する事を歓迎します。

## リリースチャンネル

| <img src="https://avatars.githubusercontent.com/in/15368?s=64&v=4" width = "30" height = "30" alt="LOGO"/> | [GitHub CI](https://github.com/KitsunePie/AppErrorsTracking/actions/workflows/commit_ci.yml) | CI 自動ビルド (テスト版) |
|------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|-----------------------------------|

| <img src="https://github.com/peter-iakovlev/Telegram/blob/public/Icon.png?raw=true" width = "30" height = "30" alt="LOGO"/> | [Telegram CI チャンネル](https://t.me/AppErrorsTracking_CI) | CI 自動ビルド (テスト版) |
|-----------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------|-----------------------------------|

| <img src="https://avatars.githubusercontent.com/in/15368?s=64&v=4" width = "30" height = "30" alt="LOGO"/> | [GitHub リリース](https://github.com/KitsunePie/AppErrorsTracking/releases) | 正式版 (安定版) |
|------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|---------------------------------|

| <img src="https://avatars.githubusercontent.com/u/78217009?s=200&v=4?raw=true" width = "30" height = "30" alt="LOGO"/> | [Xposed モジュールのリポジトリ](https://github.com/Xposed-Modules-Repo/com.fankes.apperrorstracking/releases) | 正式版 (安定版) |
|------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|---------------------------------|

このXposedモジュールのリリースは上記のURLに限定されています。

他の非公式チャンネルからダウンロードされたバージョンで及ぼした問題は一切関係はありません。

## プロモーション

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
     <h2>ねぇ、きいて! 👋</h2>
     <h3>ここでは、Androidの開発ツールやUIデザイン、Gradleプラグイン、Xposedモジュール、実用的なソフトウェアなどの関連プロジェクトを紹介します。</h3>
     <h3>もしも以下のプロジェクトであなたのお役に立てたのであれば、私にStarを付けてください!</h3>
     <h3>すべてのプロジェクトは無料でオープンソースであり、対応するオープンソースライセンス契約に基づいています。</h3>
     <h1><a href="https://github.com/fankes/fankes/blob/main/project-promote/README.md">→ 私のプロジェクトについてはこちらをクリック ←</a></h1>
</div>

## Starの推移

![Star History Chart](https://api.star-history.com/svg?repos=KitsunePie/AppErrorsTracking&type=Date)

## ライセンス

- [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.html)

```
Copyright (C) 2017 Fankes Studio(qzmmcn@163.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

Powered by [YukiHookAPI](https://github.com/HighCapable/YukiHookAPI)

Copyright © 2017 Fankes Studio(qzmmcn@163.com)
