(ns localhost.intro
  (:import spark.Spark))

(Spark/port 8080)
(Spark/externalStaticFileLocation "src/main/resources/public")
(Spark/get "/hello" (proxy [spark.Route] [] (handle [_ _] "Hello!")))
