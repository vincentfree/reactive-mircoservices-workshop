# Workshop reactive

![Kotlin CI](https://github.com/vincentfree/reactive-mircoservices-workshop/workflows/Kotlin%20CI/badge.svg)

## Benchmark

### Benchmark tools

- wrk
- gatling (maybe)

#### Benchmark samples

- Spring

wrk command

```shell script
wrk --latency -t 1 -c 25 http://localhost:8082/hello
```

output

```bash
Running 10s test @ http://localhost:8082/hello
  1 threads and 25 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     4.60ms   11.07ms 156.01ms   92.24%
    Req/Sec    15.69k     4.99k   21.29k    73.00%
  Latency Distribution
     50%    1.21ms
     75%    1.91ms
     90%   11.25ms
     99%   58.28ms
  156260 requests in 10.01s, 22.08MB read
Requests/sec:  15611.25
Transfer/sec:      2.21MB
```

- Vert.x

wrk command

```shell script
wrk --latency -t 1 -c 25 http://localhost:8081/hello
```

output

```bash
Running 10s test @ http://localhost:8081/hello
  1 threads and 25 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   284.25us  255.50us  11.80ms   97.61%
    Req/Sec    90.43k     5.41k   98.05k    90.00%
  Latency Distribution
     50%  248.00us
     75%  282.00us
     90%  390.00us
     99%  717.00us
  899665 requests in 10.00s, 83.22MB read
Requests/sec:  89960.00
Transfer/sec:      8.32MB
```

### sometimes

```
$ wrk --latency -t 1 -c 25 http://localhost:8081/helloworld
Running 10s test @ http://localhost:8081/helloworld
  1 threads and 25 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.22ms    9.29ms 148.34ms   98.80%
    Req/Sec   100.41k    31.34k  129.32k    86.87%
  Latency Distribution
     50%  195.00us
     75%  261.00us
     90%  454.00us
     99%   32.25ms
  987978 requests in 10.00s, 72.55MB read
Requests/sec:  98774.21
Transfer/sec:      7.25MB
```

with some tuning

```shell script
$ wrk --latency -t 1 -c 75 http://localhost:8081/helloworld
Running 10s test @ http://localhost:8081/helloworld
  1 threads and 75 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   625.49us  441.57us  22.78ms   97.48%
    Req/Sec   120.28k    11.65k  130.67k    89.00%
  Latency Distribution
     50%  567.00us
     75%  624.00us
     90%  765.00us
     99%    1.32ms
  1194935 requests in 10.00s, 87.75MB read
Requests/sec: 119460.51
Transfer/sec:      8.77MB
```

### from Azure environment

```bash
root@test-run-vertx-rest-service-test-container:/# wrk --latency -c 1000 -t 1 http://test-run-vertx-rest-service:80/timeout/1000
```

## Vertx application

```bash
Running 10s test @ http://test-run-vertx-rest-service:80/timeout/1000
  1 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.06s   160.52ms   1.95s    95.81%
    Req/Sec     0.95k   478.39     2.17k    67.86%
  Latency Distribution
     50%    1.01s
     75%    1.04s
     90%    1.12s
     99%    1.94s
  8060 requests in 10.05s, 1.28MB read
  Socket errors: connect 0, read 0, write 0, timeout 691
  17574 requests in 10.05s, 2.78MB read
  Socket errors: connect 0, read 0, write 0, timeout 26
Requests/sec:   1747.95
Transfer/sec:    283.36KB
```

### Openj9 test

```bash
Running 10s test @ http://test-run-vertx-rest-service:80/hello
  1 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.94ms    9.34ms 136.15ms   97.49%
    Req/Sec    17.53k     5.90k   26.10k    67.68%
  Latency Distribution
     50%  408.00us
     75%  610.00us
     90%    1.62ms
     99%   47.70ms
  173521 requests in 10.05s, 16.05MB read
Requests/sec:  17263.47
Transfer/sec:      1.60MB
```

### Same with GraalVM

```bash

```

## Spring application

```bash
Running 10s test @ http://test-run-spring-rest-service:80/timeout/1000
  2 threads and 2000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.47s   395.99ms   2.00s    47.02%
    Req/Sec   103.53     85.64   406.00     74.14%
  Latency Distribution
     50%    1.29s
     75%    1.91s
     90%    2.00s
     99%    2.00s
  1446 requests in 10.27s, 259.83KB read
  Socket errors: connect 0, read 0, write 0, timeout 1278
Requests/sec:    140.86
Transfer/sec:     25.31KB
```



----

### performance with RSS

Spring REST service

```shell script
$ wrk --latency -t 1 -c 75 http://localhost:8082/helloworld
Running 10s test @ http://localhost:8082/helloworld
  1 threads and 75 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.86ms  703.40us  13.49ms   74.93%
    Req/Sec    31.55k     2.55k   38.24k    68.00%
  Latency Distribution
     50%    1.95ms
     75%    2.18ms
     90%    2.54ms
     99%    3.69ms
  315049 requests in 10.04s, 37.91MB read
Requests/sec:  31374.27
Transfer/sec:      3.78MB
```

used **781,92MB**

Settings with xms 2G and xmx 2G

```shell script
$ wrk --latency -t 1 -c 75 http://localhost:8082/helloworld
Running 10s test @ http://localhost:8082/helloworld
  1 threads and 75 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.85ms  769.16us  21.99ms   74.65%
    Req/Sec    30.40k     2.48k   37.58k    67.00%
  Latency Distribution
     50%    1.91ms
     75%    2.17ms
     90%    2.64ms
     99%    4.41ms
  303498 requests in 10.04s, 36.52MB read
Requests/sec:  30235.37
Transfer/sec:      3.64MB
```

used **218 MB**

settings xms200m xmx200m

----

```shell script
$ wrk --latency -t 1 -c 75 http://localhost:8081/helloworld
Running 10s test @ http://localhost:8081/helloworld
  1 threads and 75 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   633.76us  247.40us  12.08ms   83.91%
    Req/Sec   117.32k     9.20k  126.92k    90.00%
  Latency Distribution
     50%  551.00us
     75%  720.00us
     90%    0.95ms
     99%    1.30ms
  1166274 requests in 10.00s, 85.64MB read
Requests/sec: 116593.31
Transfer/sec:      8.56MB
```

used **1.069,03 MB**

Settings with xms 2G and xmx 2G

```shell script
$ wrk --latency -t 1 -c 75 http://localhost:8081/helloworld
Running 10s test @ http://localhost:8081/helloworld
  1 threads and 75 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   630.89us  475.34us  24.59ms   97.31%
    Req/Sec   120.64k    14.56k  131.68k    93.00%
  Latency Distribution
     50%  567.00us
     75%  615.00us
     90%  772.00us
     99%    1.58ms
  1199149 requests in 10.00s, 88.06MB read
Requests/sec: 119869.07
Transfer/sec:      8.80MB
```

used **440,11 MB**

Settings with xms 200M and xmx 200M


### Vert.x with native transport (BSD)

```shell script
$ wrk --latency -t 1 -c 75 http://localhost:8081/helloworld
Running 10s test @ http://localhost:8081/helloworld
  1 threads and 75 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   599.95us  237.42us  16.22ms   97.10%
    Req/Sec   123.28k    10.74k  130.76k    92.00%
  Latency Distribution
     50%  566.00us
     75%  600.00us
     90%  661.00us
     99%    1.09ms
  1226074 requests in 10.00s, 90.03MB read
Requests/sec: 122558.16
Transfer/sec:      9.00MB
``` 
