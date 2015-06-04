# Finatra Hello World Example Application

An example finatra application that highlights features of the [finatra-http](../../http) framework.

Run the example server locally with sbt:
-----------------------------------------------------------

```
$ cd server
$ gradle run
```

Browse to: [http://localhost:8888/hi?name=foo](http://localhost:8888/hi?name=foo)

For json response

```
curl -v -X POST http://localhost:8888/hello --data '{"name":"foo"}'
```

Or to view the [twitter-server admin interface](https://twitter.github.io/twitter-server/Features.html#http-admin-interface): [http://localhost:9990/admin](http://localhost:9990/admin)

