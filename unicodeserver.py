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
