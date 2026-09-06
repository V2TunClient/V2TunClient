#!/data/data/com.termux/files/usr/bin/bash
set -e
ROOT="$(pwd)"
ASSET_DIR="${1:-V2TunClient-logo-update}"

if [ ! -d "$ASSET_DIR" ]; then
  echo "Folder not found: $ASSET_DIR"
  exit 1
fi

cp "$ASSET_DIR/art/v2tunclient_logo_512.png" art/v2tunclient_logo_512.png
cp "$ASSET_DIR/art/v2tunclient_foreground_432.png" art/v2tunclient_foreground_432.png
cp "$ASSET_DIR/app/src/main/ic_launcher-web.png" app/src/main/ic_launcher-web.png

for d in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
  cp "$ASSET_DIR/app/src/main/res/mipmap-$d/ic_launcher.png" "app/src/main/res/mipmap-$d/ic_launcher.png"
  cp "$ASSET_DIR/app/src/main/res/mipmap-$d/ic_launcher_round.png" "app/src/main/res/mipmap-$d/ic_launcher_round.png"
  cp "$ASSET_DIR/app/src/main/res/mipmap-$d/ic_launcher_foreground.png" "app/src/main/res/mipmap-$d/ic_launcher_foreground.png"
done

git add art app/src/main/ic_launcher-web.png app/src/main/res/mipmap-*/ic_launcher*.png
git commit -m "Refresh V2TunClient app icon"
echo "Logo installed and committed."
