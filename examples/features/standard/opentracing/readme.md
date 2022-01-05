# Opentracing Plugin Example

This plugin
embraces [OpenTelemetry Autoconfiguration](https://github.com/open-telemetry/opentelemetry-java/tree/main/sdk-extensions/autoconfigure)
using environment-based properties to configure OpenTelemetry SDK.

## Run Opentracing Plugin Example

To run the example, simply type **mvn verify** from this directory, or **mvn -PnoServer verify** if you want to start
and create the broker manually.
> **_NOTE:_**   You must have [Zipkin](https://github.com/open-telemetry/opentelemetry-java/tree/main/sdk-extensions/autoconfigure#zipkin-exporter) running at `http://localhost:9411`. You can learn more about zipkin [here](https://zipkin.io/)

After seeing a **`Build Success`**, open the browser, connect to your running instance of Zipkin and check for spans.

## Customise Opentracing Plugin Example

The [`tracing.properties`](./src/main/resources/tracing.properties) has configuration properties that
autoconfigure [Opentelemetry Exporter](https://github.com/open-telemetry/opentelemetry-java/tree/main/sdk-extensions/autoconfigure#exporters)
. We reconfigured it and used Zipkin as the default exporter, running at `http://localhost:9411`
You can change this by choosing to use:

- [Jeager Exporter](https://github.com/open-telemetry/opentelemetry-java/tree/main/sdk-extensions/autoconfigure#jaeger-exporter)
  , by uncommenting (removing `#`) the following
    - Jeager enabler: `otel.traces.exporter=jaeger`
    - Jeager endpoint: `otel.exporter.jaeger.endpoint=http://localhost:14250`. Change port and host to match your
      running instance.
  > **Note:** Jeager is somewhat tricky, you must make sure ports match, or otherwise it might not work.
- [otlp exporter](https://github.com/open-telemetry/opentelemetry-java/tree/1e073fcff20697fd5f2eb39bd6246d06a1231089/sdk-extensions/autoconfigure)
  , by uncommenting (removing `#`) the following
    - otlp enabler: `otel.traces.exporter=otlp`
    - otlp endpoint: `otel.exporter.otlp.endpoint=http://localhost:4317` Change port and host to match your running
      instance.
    - otlp traces-endpoint: `otel.exporter.otlp.traces.endpoint=http://localhost:4317` Change port and host to match
      your running instance.

You can also change the default service name from `opentracing_plugin` to any string by changing the value
of `otel.service.name`

## How to start exporters
- [Zipkin](https://zipkin.io/pages/quickstart): The quickest way is by use of docker.  
  - Open the terminal, copy, paste and run the command `docker run -d -p 9411:9411 openzipkin/zipkin`
  - open the browser, enter the url `http://localhost:9411` and on the page that appears, click the **Run Queries** button.
- [Jeager](https://www.jaegertracing.io/docs/1.30/getting-started/): The quickest way is by use of docker.
  - open the terminal and paste the command below 
    ```
    docker run -d --name jaeger \
    e COLLECTOR_ZIPKIN_HOST_PORT=:9411 \
    p 5775:5775/udp \
    p 6831:6831/udp \
    p 6832:6832/udp \
    p 5778:5778 \
    p 16686:16686 \
    p 14250:14250 \
    p 14268:14268 \
    p 14269:14269 \
    p 9411:9411 \
    jaegertracing/all-in-one:1.30
    ```
  - open the browser, enter the url `http://localhost:16686/search`, click **Search**, select your service-name from the dropdown below the service name and finally click **Find Traces** Button.
