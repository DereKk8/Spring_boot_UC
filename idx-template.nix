{ pkgs, ... }: {
  bootstrap = ''
    cp -rf ${./.} "$WS_NAME"
    chmod -R +w "$WS_NAME"
    mv "$WS_NAME" "$out"
  '';
}