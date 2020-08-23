## Bug report in STTP's unicode handling in scala native

When server serves `"ह"`, the JVM sttp client returns it correctly
as `"ह"` (string) or as `ByteArray(34, -32, -92, -71, 34)`.
The Scala Native sttp client, however returns it incorrectly as `"9"` (string)
or as `ByteArray(34, 57, 34)`.


Following are the repro steps:

### Run the python server that serves a single unicode letter `ह` enclosed in double quotes.
```
python unicodeserver.py
```

### Compile and run the scala native binary
```
sbt build
./target/scala-2.11/unicode-out
```

The following is the output:

```
default: '"9"', length: 3
utf8str: '"9"', length: 3
bytearray: List(34, 57, 34)
```

### Run the equivalent scala script with ammonite
```
amm jvm-version.sc
```

The following is the outut:

```
default: '"ह"', length: 3
utf8str: '"ह"', length: 3
bytearray: List(34, -32, -92, -71, 34)
```

### Expectation
Native code should produce the same output as the JVM version

### Details
```
ammonite: 1.6.9, scala(2.13.0), java(1.8.0_144)
native: sbt(0.13.15), scala(2.11.11), scala-native(0.4.0-M2)
```

### Revelant file content replicated below for quick glance

`unicodeserver.py`
```python
import http.server
import socketserver

PORT = 9988

class Handler(http.server.SimpleHTTPRequestHandler):
  def do_GET(self):
    self.send_response(200)

    self.send_header('Content-type', 'application/json; charset=UTF-8')
    self.send_header('Cache-Control', 'no-store, must-revalidate')
    self.end_headers() 

    bytes_to_send = '"ह"'.encode('utf-8')

    print(bytes_to_send)
    self.wfile.write(bytes_to_send)


def serve(port=PORT):
  with socketserver.TCPServer(('', port), Handler) as httpd:
    httpd.serve_forever()


serve()
```

`jvm-version.sc` (Scala JVM code)
```
import $ivy.`com.softwaremill.sttp.client::core:2.2.5`
import sttp.client.quick._

def req = quickRequest.get(uri"http://localhost:9988")

val default = req.send().body
println(s"default: '$default', length: ${default.length}")

val utf8str = req.response(asString("UTF-8")).send().body.right.get
println(s"utf8str: '$utf8str', length: ${utf8str.length}")

val byteArray = req.response(asByteArray).send().body.right.get.toList
println(s"bytearray: $byteArray")
```

`SttpUnicode.scala` (Scala Native code)
```
package unicode

import sttp.client.quick._

object SttpUnicode {
  def main(args: Array[String]): Unit = {
    def req = quickRequest.get(uri"http://localhost:9988")

    val default = req.send().body
    println(s"default: '$default', length: ${default.length}")

    val utf8str = req.response(asString("UTF-8")).send().body.right.get
    println(s"utf8str: '$utf8str', length: ${utf8str.length}")

    val byteArray = req.response(asByteArray).send().body.right.get.toList
    println(s"bytearray: $byteArray")
  }
}
```
