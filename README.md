# Extensible Effects Akka-Http Slick
The Extensible Effects Akka-Http Slick is a simple json rest api to act as practical example of one way to use the Eff monad and extensible effects on an scala application.

It supports the following features:

* Extensible effects, using [eff](https://github.com/atnos-org/eff)
* Generic Data Access layer, using [slick-repo](https://github.com/gonmarques/slick-repo)
* DI with Guice, using [scala-guice](https://github.com/codingwell/scala-guice)
* Flyway for database schema evolution
* Tests for routes and services with [ScalaTest](http://www.scalatest.org/)

Utils: 

* Typesafe config for property management
* Typesafe Scala Logging (LazyLogging)

#### Running

The application is pre-configured to use a H2 In-memory database instance. 

So, to run you just have to:


        $ sbt run
        
        
        

###### Another database alternative
To use another type of Slick supported database, ensure you have your database server running, 
create a new database on it and configure it on ```src/main/resources/application.conf``` accordingly.

There's already a configuration example for postgreSQL on the file.


#### Testing

To run all tests:


        $ sbt test

#### API Usage

Creating data:
```
curl -X POST \
    http://localhost:9881/users/0d3fcc37-d330-4c7c-8d82-128235617d7d/keys \
    -H 'Content-Type: application/json' \
    -d '{"value":"the_key" }'
```

Getting data: 
```
curl -X GET \
  http://localhost:9881/users/0d3fcc37-d330-4c7c-8d82-128235617d7d/keys 
```

Deleting data:
```
curl -X DELETE \
  http://localhost:9881/users/0d3fcc37-d330-4c7c-8d82-128235617d7d/keys/{key_uuid} \
```

### Extensible Effects Resources
If you're interested in additional information on the Eff monad and extensible effects these are some good resources you might consider looking into:

* Oleg Kiselyov's [Freer monads, more extensible effects](http://okmij.org/ftp/Haskell/extensible/more.pdf)
* Eric Torreborre's talk [The Eff monad, one monad to rule them all](https://vimeo.com/channels/flatmap2016/165927840)
* Eff library [documentation](http://atnos-org.github.io/eff/index.html)
* [A Journey into Extensible Effects in Scala](https://rea.tech/author/jack-low/) by Jack Low

### Credits

To everyone working on the on the eff library, and also slick, akka-http, slick-repo, and ScalaTest. Please consider contributing to these projects and open-source in general. 
