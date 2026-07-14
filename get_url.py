import urllib.request
import sys
req = urllib.request.Request(sys.argv[1], headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'})
try:
    print(urllib.request.urlopen(req).read().decode('utf-8'))
except Exception as e:
    print(e)
