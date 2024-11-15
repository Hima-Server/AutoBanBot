# Minecraft 自動BANプラグイン

このMinecraftプラグインは、設定された行動をしたプレイヤーを自動的にBANします。各行動に対してカスタムBAN理由を設定でき、BANされたプレイヤーの詳細は`banlist.yml`ファイルに保存されます。

対応バージョンは1.16.5～1.21です。
## 機能
- 特定の行動に対してプレイヤーをBANします：
  - アイテムをドロップしたとき
  - ネザーやエンドに入ったとき
  - TNTを設置したとき
  - チャットスパム（5秒以内に5メッセージ以上のチャット）
- `config.yml`で各行動ごとのBAN理由をカスタマイズ可能。
- BANされたプレイヤーの情報（MCID、UUID、BAN日時、IP、BAN理由）を`banlist.yml`に記録します。

## インストール方法
1. Javaコードをコンパイルして、`.jar`ファイルを作成します。
2. `.jar`ファイルをサーバーの`plugins`フォルダに配置します。
3. サーバーを再起動またはリロードします。
4. プラグインフォルダに`config.yml`ファイルが生成されるので、必要に応じて設定を変更してください。

## 設定

### config.yml
どの行動でBANを実行するか、およびBAN理由を`config.yml`でカスタマイズできます。設定例は以下の通りです。

```yaml
ban-commands:
  - "/example" # BAN対象のコマンド
  - "/test"

ban-on:
  item-drop: true           # アイテムドロップでBAN
  nether-entry: true        # ネザーに入るとBAN
  end-entry: true           # エンドに入るとBAN
  tnt-place: true           # TNT設置でBAN
  spam-chat: true           # 5秒以内に5回以上のチャットでBAN

reason:
  item-drop: "アイテムをドロップしたためBANされました！"
  nether-entry: "ネザーに入ったためBANされました！"
  end-entry: "エンドに入ったためBANされました！"
  tnt-place: "TNTを設置したためBANされました！"
  spam-chat: "チャットスパムのためBANされました！"
  command: "禁止コマンドを使用したためBANされました！"
```

- **ban-commands**: 使用するとBANされるコマンドのリスト。
- **ban-on**: 各行動に対してBANを有効または無効に設定。
- **reason**: 各行動に対するカスタムBANメッセージを設定。

### banlist.yml
プレイヤーがBANされると、自動的に`banlist.yml`が生成・更新されます。UUIDごとに以下の情報が保存されます：
- **MCID**（Minecraft ID）
- **UUID**（ユーザーのユニークID）
- **Date**（BANの日時）
- **IP**（プレイヤーのIPアドレス）
- **Reason**（BAN理由）

`banlist.yml`の例：
```yaml
banned-players:
  <UUID>:
    MCID: "<プレイヤー名>"
    UUID: "<プレイヤーUUID>"
    Date: "2024-11-15 13:45:22"
    IP: "123.45.67.89"
    Reason: "TNTを設置したためBANされました！"
```

## イベント詳細

| イベント       | 設定キー          | デフォルトBAN理由                        |
|----------------|--------------------|------------------------------------------|
| アイテムドロップ  | item-drop         | "アイテムをドロップしたためBANされました！" |
| ネザー侵入      | nether-entry       | "ネザーに入ったためBANされました！"       |
| エンド侵入      | end-entry          | "エンドに入ったためBANされました！"       |
| TNT設置       | tnt-place          | "TNTを設置したためBANされました！"       |
| チャットスパム  | spam-chat          | "チャットスパムのためBANされました！"     |
| コマンド使用    | command            | "禁止コマンドを使用したためBANされました！" |

## ライセンス
このプラグインはMITライセンスの下で公開されています。
