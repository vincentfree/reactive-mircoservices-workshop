# Workshop reactive


## Benchmark

### Benchmark tools

- wrk
- gatling (maybe)

#### Benchmark samples

* Spring

wrk command
```shell script
$ wrk --latency -t 1 -c 25 http://localhost:8082/hello
```

output

```
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

* Vert.x

wrk command
```shell script
$ wrk --latency -t 1 -c 25 http://localhost:8081/hello
```

output

```
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