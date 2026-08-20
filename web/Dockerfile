FROM nginx:1.27-alpine

COPY web/ /usr/share/nginx/html/
COPY deploy/nginx/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
