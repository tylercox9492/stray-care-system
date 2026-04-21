FROM nginx:1.27-alpine

COPY *.html /usr/share/nginx/html/
COPY *.css /usr/share/nginx/html/
COPY *.js /usr/share/nginx/html/
COPY images /usr/share/nginx/html/images
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY docker-entrypoint.d/40-write-config.sh /docker-entrypoint.d/40-write-config.sh

RUN chmod +x /docker-entrypoint.d/40-write-config.sh

EXPOSE 80
