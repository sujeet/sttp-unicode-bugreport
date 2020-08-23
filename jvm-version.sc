import $ivy.`com.softwaremill.sttp.client::core:2.2.5`
import sttp.client.quick._

def req = quickRequest.get(uri"http://localhost:9988")

val default = req.send().body
println(s"default: '$default', length: ${default.length}")

val utf8str = req.response(asString("UTF-8")).send().body.right.get
println(s"utf8str: '$utf8str', length: ${utf8str.length}")

val byteArray = req.response(asByteArray).send().body.right.get.toList
println(s"bytearray: $byteArray")
