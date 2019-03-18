## Links

[Prometheus Histograms](https://prometheus.io/docs/practices/histograms/)

Prometheus Service Discovery
[Configuration](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)
[Custom SD](https://prometheus.io/blog/2018/07/05/implementing-custom-sd/)

To reload Prometheus config, you can run the following command instead of restarting:

`curl -X POST http://localhost:9090/-/reload`
