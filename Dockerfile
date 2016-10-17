FROM clojure:lein-2.5.3

MAINTAINER Dan Pomohaci <dan.pomohaci@gmail.com>

EXPOSE 3000

RUN mkdir -p /usr/src/app

COPY ./src /usr/src/app/src/

COPY ./test /usr/src/app/test/

COPY ./project.clj /usr/src/app/project.clj

WORKDIR /usr/src/app

RUN lein deps

VOLUME /usr/src/app/config/

CMD ["lein", "run"]
