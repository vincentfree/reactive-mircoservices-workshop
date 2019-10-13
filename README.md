# Workshop reactive


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

```txt
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

```txt
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


### from environment

```txt
root@test-run-vertx-rest-service-test-container:/# wrk --latency -c 1000 -t 1 http://test-run-vertx-rest-service:80/timeout/1000

## Vertx application

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

root@test-run-vertx-rest-service-test-container:/# wrk --latency -c 2000 -t 2 http://test-run-spring-rest-service:80/timeout/1000

## Spring application

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
