from http.server import HTTPServer, BaseHTTPRequestHandler
import json
from datetime import datetime
import socket  # Added for IP and hostname lookup

class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):
    def log_request(self, code='-', size='-'):
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        print(f'[{now}] {self.command} {self.path} - {code}')

    def do_GET(self):
        if self.path == '/':
            welcome_html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Welcome Page</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 20px;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        background-color: #f0f2f5;
                    }
                    .container {
                        background-color: white;
                        padding: 40px;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        text-align: center;
                    }
                    h1 {
                        color: #1a73e8;
                        margin-bottom: 20px;
                    }
                    p {
                        color: #5f6368;
                        margin-bottom: 30px;
                    }
                    .logout-button {
                        background-color: #1a73e8;
                        color: white;
                        padding: 10px 20px;
                        border: none;
                        border-radius: 4px;
                        cursor: pointer;
                        font-size: 16px;
                    }
                    .logout-button:hover {
                        background-color: #1558b0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Welcome to SKALA World</h1>
                    <p>Thank you for visiting your SKALA!</p>
                    <p>Request received at: """ + datetime.now().strftime('%Y-%m-%d %H:%M:%S') + """</p>
                    <form action="/logout" method="get">
                        <button type="submit" class="logout-button">Logout</button>
                    </form>
                </div>
            </body>
            </html>
            """
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(welcome_html.encode('utf-8'))
        elif self.path == '/logout':
            # 네이버로 리다이렉트
            self.send_response(302)
            self.send_header('Location', 'https://www.naver.com')
            self.end_headers()
        elif self.path == '/pod-ip':
            # Get pod IP address
            hostname = socket.gethostname()
            ip_address = socket.gethostbyname(hostname)

            self.send_response(200)
            self.send_header('Content-type', 'text/plain')
            self.end_headers()
            self.wfile.write(f"{ip_address}\n".encode('utf-8'))  # 줄바꿈 추가
        elif self.path == '/pod-name':
            # Get pod hostname
            hostname = socket.gethostname()

            self.send_response(200)
            self.send_header('Content-type', 'text/plain')
            self.end_headers()
            self.wfile.write(f"{hostname}\n".encode('utf-8'))  # 줄바꿈 추가
        else:
            self.send_response(404)
            self.send_header('Content-type', 'text/plain')
            self.end_headers()
            self.wfile.write(b'404 Not Found')

def run_server(port=8080):
    server_address = ('', port)
    httpd = HTTPServer(server_address, SimpleHTTPRequestHandler)
    print(f'Starting server on port {port}...')
    print(f'Access the server at http://localhost:{port}/login')
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print('\nShutting down server...')
        httpd.server_close()

if __name__ == '__main__':
    run_server()
