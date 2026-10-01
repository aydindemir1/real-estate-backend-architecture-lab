FROM openresty/openresty:alpine

ARG CASSANDRA_ADMIN_COMMIT=af53c2d2b7c9db7673affca7f5791fa3d7551491

WORKDIR /app

USER root

RUN apk add --no-cache git luarocks5.1 \
    && ln -sf /usr/bin/luarocks-5.1 /usr/bin/luarocks \
    && luarocks install lua-resty-template \
    && luarocks install lua-resty-reqargs \
    && luarocks install inspect \
    && git clone https://github.com/IBM/cassandra-admin.git /tmp/cassandra-admin \
    && cd /tmp/cassandra-admin \
    && git checkout "${CASSANDRA_ADMIN_COMMIT}" \
    && mkdir -p /app /lualib \
    && cp -R src/. /app/ \
    && cp -R lib/. /lualib/ \
    && rm -rf /tmp/cassandra-admin \
    && adduser -D -g 'appuser' appuser \
    && mkdir -p /run/app/logs /etc/cassandra-admin \
    && chown -R appuser:appuser /run/app /etc/cassandra-admin /app /lualib

USER appuser

CMD ["/bin/sh", "-c", "/app/_docker_entrypoint.lua > /run/app/nginx.conf && exec nginx -p /run/app -c /run/app/nginx.conf"]
