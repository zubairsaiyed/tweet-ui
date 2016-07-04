$(function() {

    tracking = new Set();
    refreshInterval = 1000;
    timeout = null;

	// draw Highchart
    $(document).ready(function() {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });

        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                type: 'spline',
                animation: Highcharts.svg, // don't animate in old IE
                marginRight: 10
            },
            title: {
                text: ''
            },
            xAxis: {
                type: 'datetime',
                tickPixelInterval: 150
            },
            yAxis: {
                title: {
                    text: 'Sentiment'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }],
                min: -1,
                max: 1
            },
            tooltip: {
                formatter: function() {
                    return '<b>' + this.series.name + '</b><br/>' +
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                        Highcharts.numberFormat(this.y, 2);
                }
            },
            legend: {
                enabled: true
            },
            exporting: {
                enabled: false
            },

            plotOptions: {
                series: {
                    marker: {
                        enabled: false
                    }
                }
            },
        });
    });

	// submit user query
    $("#submitQuery").click(function(e) {
        e.preventDefault();
        var query = $("#inputSearchQuery").val().trim();

		// submit new query keyword using POST request, track query locally as well
        if (query.length > 0) {
            $.post("/data/", query);
            tracking.add(query);
        }

		// reset query input
        $("#inputSearchQuery").val("");

		// initiate continous data retrieval
        if (timeout == null)
            fetchData();
    });

	// fetch data from server (continuously)
    function fetchData() {

		// generate GET url
        var url = "/data/?";
        for (query of tracking) {
            url += "q=" + encodeURI(query) + "&";
        }
        url.substring(0, url.length - 1);
        $.getJSON(url, function(data) {

			// update Highchart with new data points
            var currTime = (new Date()).getTime();
            $.each(data, function(index, query) {
                if (query !== null && query.name !== null) {
                    var series = chart.get(query.name);
                    if (series === null) {
                        series = chart.addSeries({
                            name: query.name,
                            id: query.name
                        }, false);
                    }
                    if (query !== null && query.data <= 1) {
                        var shift = series.data.length > 100;
                        series.addPoint([currTime, query.data], false, shift);
                    }
                }
            });
            chart.redraw();
        });

		// repeat GET request after 'refreshInterval'
        timeout = setTimeout(fetchData, refreshInterval);
    }

});
