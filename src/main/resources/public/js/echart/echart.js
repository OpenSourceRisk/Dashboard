
/*
 Copyright (C) 2016 Quaternion Risk Management Ltd
 All rights reserved.

 This file is part of ORE, a free-software/open-source library
 for transparent pricing and risk analysis - http://opensourcerisk.org

 ORE is free software: you can redistribute it and/or modify it
 under the terms of the Modified BSD License.  You should have received a
 copy of the license along with this program.
 The license is also available online at <http://opensourcerisk.org>

 This program is distributed on the basis that it will form a useful
 contribution to risk analytics and model standardisation, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE. See the license for more details.
*/

var BARCharts = (function () {
    "use strict";
    var instance;

    function init() {

        function setNewData(chart_, data_) {
            var localOptions_ = options;

            var dataStyle = {
                normal: {
                    label : {
                        show : true,
                        position: 'insideLeft',
                        formatter: function(params) {
                            // console.debug(params);
                            return params.seriesName == 'limits' ? '' : params.name;
                        }, //'{b}',
                        textStyle : {
                            color: 'black'
                        }
                    },
                    color: function(params) {
                        // red '#C1232B'
                        // salmon '#FE8463'
                        var colorList = [
                            'rgba(120,178,88,0.5)', 'rgba(255, 69, 0, 0.5)'
                        ];
                        return colorList[params.seriesIndex]
                    }
                }
            };

            if (1 == chartManager.getLimits()) {
                localOptions_.series = [
                    {
                        itemStyle: dataStyle,
                        type: data_.seriesType,
                        data: data_.yaxisValues,
                        name: 'bar data'
                    },
                    {
                        itemStyle: dataStyle,
                        type: data_.seriesType,
                        data: data_.yaxisLimits,
                        name: 'limits'
                    }
                ];
                localOptions_.legend.data = ['limits'];
            } else {
                localOptions_.series = [
                    {
                        itemStyle: dataStyle,
                        type: data_.seriesType,
                        data: data_.yaxisValues,
                        name: 'bar data'
                    }
                ];
            }
            localOptions_.title = [{text: data_.title, subtext: data_.subTitleText}];
            localOptions_.yAxis[0].data = data_.yaxisLabels;
            localOptions_.yAxis[0].axisLabel = {show:false};

            chart_.setOption(localOptions_, true);
        }

        var options = {
            tooltip: {
                trigger: 'axis',
                formatter: barChartTooltipFormatter
            },
            legend: {
                data: null
            },
            toolbox: {
                show: false,
                feature: {
                    dataView: {
                        show: false, title: 'view data', lang: ['Data', 'ok', 'refresh']
                    },
                    magicType: {
                        show: true,
                        title: {
                            line: 'Line',
                            bar: 'Bar',
                            stack: 'Stack',
                            tiled: 'Tiled'
                        },
                        type: ['stack','tiled']
                    },
                    restore: {
                        show: true,
                        title: "Restore"
                    },
                    saveAsImage: {
                        show: true,
                        title: "Save Image"
                    }
                }
            },
            calculable: true,
            clickable: true,
            xAxis: [{
                type: 'value',
                boundaryGap: false,
                axisLabel:{interval: 'auto', formatter: function(value){
                    return numeral(value).format('(0.0a)');
                }},
            }],
            yAxis: [{
                type: 'category',
                boundaryGap: true,
                axisLabel: {
                    interval: 'auto'
                    , formatter: function (value) {
                        return value;
                    }
                },
                data: []
            }],
            series: null
        };

        function initialiseAllCharts() {
            // set the initial entry point to 'Total' level
            var args = {
                mode: chartManager.getMode(),
                hierarchy: chartManager.getHierarchy(),
                node: chartManager.getNode(),
                xvaHierarchy: chartManager.getHierarchy(),
                xvaNode: chartManager.getNode()
            };

            barGraphs.forEach(function(elem){
                var p_ = chartManager.getGraphData(args, elem.metric, 'bargraph');
                chartManager.initChart(elem.id, options, p_, setNewData, chartManager.barGraphIsClickable());

                p_.then(function(res){
                    // lookup the category in the chartCategories array
                    var cat_ = filter(chartCategory, function(e){return e.metric == elem.metric;});
                    document.getElementById(elem.id).parentNode.parentNode.getElementsByTagName('h2')[0].innerText = cat_[0].category;
                });

            });


        }

        function loadData(chart_, data_) {
            setNewData(chart_, data_);
        }

// expose a few methods and properties
        return {
            initAllCharts: initialiseAllCharts,
            setNewData: setNewData,
            loadData: loadData,
            getDefaults: options
        };
    }

    return {
        // Get the Singleton instance if one exists
        // or create one if it doesn't
        getInstance: function () {
            if (!instance) {
                instance = init();
            }
            return instance;
        }
    };

})();
var LINECharts = (function () {

    'use strict';
    var instance;

    function init() {

        var line_total_exposure, line_exposure_profile;
        var baseccy_ = chartManager.getBaseCcy();

        var total_exposure_options = {

            title: {
                text: 'Total Exposure  ' + baseccy_,
                subtext: 'Historical Credit Exposure Trends: NPV, CE, EEPE'
            },
            tooltip: {
                trigger: 'axis',
                formatter: lineChartTooltipFormatter
            },
            legend: {
                x: 140,
                y: 40,
                data: []
            },
            toolbox: {
                show: true,
                feature: {
                    dataView: {
                        show: false, title: 'view data', lang: ['Data', 'ok', 'refresh']
                    },
                    magicType: {
                        show: true,
                        title: {
                            line: 'Line',
                            bar: 'Bar',
                            stack: 'Stack',
                            tiled: 'Tiled'
                        },
                        type: ['line', 'bar']
                    },
                    restore: {
                        show: true,
                        title: "Restore"
                    },
                    saveAsImage: {
                        show: true,
                        title: "Save Image"
                    }
                }
            },
            dataZoom: {
                show: true,
                realtime: true,
                start: 0,
                end: 100
            },
            xAxis: [{
                type: 'time',
                axisLabel:{interval: 'auto', formatter: function(value){
                    return moment(value).format('DD-MM-YYYY');
                }},

                // boundaryGap: false,
                data: []
            }],
            yAxis: [{
                type: 'value',
                axisLabel:{interval: 'auto', formatter: function(value){
                    return numeral(value).format('(0.0a)');
                }},
            }],
            series: []
        };

        var exposure_profile_options = {
            title: {
                text: 'Exposure Profile  '  + baseccy_,
                subtext: 'Simulated EPE, ENE & PFE'
            },
            tooltip: {
                trigger: 'axis',
                formatter: lineChartTooltipFormatter
            },
            legend: {
                x: 220,
                y: 40,
                data: []
            },
            toolbox: {
                show: true,
                feature: {
                    magicType: {
                        show: true,
                        title: {
                            line: 'Line',
                            bar: 'Bar',
                            stack: 'Stack',
                            tiled: 'Tiled'
                        },
                        type: ['line', 'bar']
                    },
                    restore: {
                        show: true,
                        title: "Restore"
                    },
                    saveAsImage: {
                        show: true,
                        title: "Save Image"
                    }
                }
            },
            dataZoom: {
                show: true,
                realtime: true,
                start: 0,
                end: 100
            },
            xAxis: [{
                type: 'time',
                boundaryGap: true,
                axisLabel:{interval: 'auto', formatter: function(value){
                    return moment(value).format('DD-MM-YYYY');
                }},
                data: []
            }],
            yAxis: [{
                type: 'value',
                axisLabel:{interval: 'auto', formatter: function(value){
                    return numeral(value).format('(0.0a)');
                }},

            }],
            series: []
        };

        function setNewData(chart_, data_) {
            if (chart_.getOption().title[0].text.startsWith('Exposure')){
                // yuk
                var xAxisData_ = data_.dates;

                var data_epes = [];
                var data_pfes = [];
                var data_enes = [];

                // merge in the dates with the data for x axis labels
                for (var j = 0;j< xAxisData_.length;j++){
                    xAxisData_[j] = moment(xAxisData_[j], 'YYYYMMDD').toDate();
                    data_epes.push([xAxisData_[j], data_.epes[j]]);
                    data_pfes.push([xAxisData_[j], data_.pfes[j]]);
                    data_enes.push([xAxisData_[j], data_.enes[j]]);
                }

                var series_0 = {
                    name: "EPE",
                    data: data_epes
                }
                var series_1 = {
                    name: "PFE",
                    data: data_pfes
                }
                var series_2 = {
                    name: "ENE",
                    data: data_enes
                }
                var series_ = [];
                series_.push(series_0);
                series_.push(series_1);
                series_.push(series_2);

                series_.forEach(function (elem) {
                    elem.type = 'line';
                    elem.smooth = true;
                    elem.symbolSize = 1;
                    elem.itemStyle = {
                        normal: {
                            areaStyle: {
                                type: 'default'
                            }
                        }
                    };
                });
                var localOptions_ = exposure_profile_options;
                localOptions_.legend.data = data_.name;
                localOptions_.xAxis[0].data = xAxisData_;
                localOptions_.series = series_;
                chart_.setOption(localOptions_, true);

            } else {
                var xAxisData_ = data_.dates;

                var data_npvs = [];
                var data_ces = [];
                var data_eepes = [];
                //var data_tes = [];

                // merge in the dates with the data for x axis labels
                for (var j = 0;j< xAxisData_.length;j++){
                    xAxisData_[j] = moment(xAxisData_[j], 'YYYYMMDD').toDate();
                    data_npvs.push([xAxisData_[j], data_.npvs[j]]);
                    data_ces.push([xAxisData_[j], data_.ces[j]]);
                    data_eepes.push([xAxisData_[j], data_.eepes[j]]);
                    //data_tes.push([xAxisData_[j], data_.tes[j]]);
                }

                var series_0 = {
                    name: "NPV",
                    data: data_npvs
                }
                var series_1 = {
                    name: "CE",
                    data: data_ces
                }

                var series_2 = {
                    name: "EEPE",
                    data: data_eepes
                }
                //var series_3 = {
                //    name: "Total",
                //    data: data_tes
                //}

                var series_ = []
                series_.push(series_0);
                series_.push(series_1);
                series_.push(series_2);
                // series_.push((series_3));

                series_.forEach(function (elem) {
                    if (1 == chartManager.getLimits()) {
                        switch (elem.name) {
                            case 'CE' :
                                elem.markLine = {
                                    symbol: ['none','none'],
                                    data: [
                                        {yAxis: +data_.limitCE, name: 'limit', itemStyle: {normal: {color: '#dc143c',
                                            label: {
                                                show: false
                                            },
                                            labelLine: {
                                                show: false
                                            }
                                        }}}
                                    ]};
                                break;
                            case 'EEPE':
                                elem.markLine = {
                                    symbol: ['none','none'],
                                    data: [
                                        {yAxis: +data_.limitEEPE, name: 'limit', itemStyle: {normal: {color: '#dc143c'
                                            ,label: {
                                                show: false
                                            },
                                            labelLine: {
                                                show: false
                                            }
                                    }}}
                                    ]};
                                break;
                            default:
                                break;
                        }
                    }

                    elem.type = 'line';
                    elem.smooth = true;
                    // elem.showAllSymbol= false;
                    elem.symbolSize = 1;
                    elem.itemStyle = {
                        normal: {
                            areaStyle: {
                                type: 'default'
                            }
                        }
                    };
                });

                var localOptions_ = total_exposure_options;
                series_.forEach(function(elem){
                    localOptions_.legend.data.push(elem.name);
                });

                localOptions_.series = series_;
                chart_.setOption(localOptions_, true);
            }
        }

        function initialiseAllCharts() {
            StackTrace.get().then(StackTraceCallback).catch(StackTraceErrback);
            var args = {
                mode: chartManager.getMode(),
                hierarchy: chartManager.getHierarchy(),
                node: chartManager.getNode(),
                xvaHierarchy: chartManager.getHierarchy(),
                xvaNode: chartManager.getNode()
            };
            var totexp_ = chartManager.getGraphData(args, '', 'totalexposure');
            var exp_ = chartManager.getGraphData(args, '', 'exposure');

            return Promise.all([totexp_, exp_]).then(function (values) {
                chartManager.initChart('line_total_exposure', total_exposure_options, totexp_, setNewData, false);
                chartManager.initChart('line_exposure_profile', exposure_profile_options, exp_, setNewData, false);
                return 'done';
            }).catch(function(error){
                console.error(new Error(error));
            })
        }

        function loadData(chart_, data_) {
            setNewData(chart_, data_);
        }

        // expose a few methods and properties
        return {
            initAllCharts: initialiseAllCharts,
            setNewData: setNewData,
            loadData: loadData,
            getDefaultExposureOpts : exposure_profile_options,
            getDefaultTotalOptions: total_exposure_options
        };
    }

    return {
        // Get the Singleton instance if one exists
        // or create one if it doesn't
        getInstance: function () {
            if (!instance) {
                instance = init();
            }
            return instance;
        }
    };

})();
var DONUTCharts = (function () {

    'use strict';
    var instance;

    function init() {

        var donut_cva, donut_fva, donut_colva;
        var options = {
            tooltip: {
                trigger: 'item',
                // formatter: "{a} <br/>{b} : {c} ({d}%)"
                formatter: donutChartTooltipFormatter
            },
            calculable: true,
            legend: {
                show: false,
                x: 'center',
                y: 'bottom',
                data: []
            },
            toolbox: {
                show: false,
                feature: {
                    magicType: {
                        show: true,
                        type: ['pie', 'funnel'],
                        option: {
                            funnel: {
                                x: '25%',
                                width: '50%',
                                funnelAlign: 'center',
                                max: 1548
                            }
                        }
                    },
                    restore: {
                        show: true,
                        title: "Restore"
                    },
                    saveAsImage: {
                        show: true,
                        title: "Save Image"
                    }
                }
            },
            series: [{
                name: '',
                type: 'pie',
                radius: ['40%', '60%'],
                itemStyle: {
                    normal: {
                        label: {
                            show: true
                        },
                        labelLine: {
                            show: true
                        }
                    },
                    emphasis: {
                        label: {
                            show: true,
                            position: 'center',
                            textStyle: {
                                fontSize: '14',
                                fontWeight: 'normal'
                            }
                        }
                    }
                },
                data: []
            }]
        };

        function setNewData(chart_, data_) {
            chart_.setOption({
                series: [{
                    data: data_.data,
                    name: data_.name
                }],
                legend: {data: data_.labels}
            });
        }

        function initialiseAllCharts() {
            var args = {
                mode: chartManager.getMode(),
                hierarchy: chartManager.getHierarchy(),
                node: chartManager.getNode(),
                xvaHierarchy: chartManager.getHierarchy(),
                xvaNode: chartManager.getNode()
            };

            xvaGraphs.forEach(function(elem){
                var p_ = chartManager.getGraphData(args, elem.metric,'xva');
                chartManager.initChart(elem.name, options, p_, setNewData, true);
                p_.then(function(res){
                    var titleText_ = elem.text + " : " + chartManager.getBaseCcy() + ' ' + numeral(res.sum).format('(0.00a)');
                    document.getElementsByName(elem.name)[0].innerText = titleText_;
                });

            });
        }

        function loadData(chart_, data_) {
            setNewData(chart_, data_);
        }

// expose a few methods and properties
        return {
            initAllCharts: initialiseAllCharts,
            setNewData: setNewData,
            loadData: loadData,
            getDefaults: options
        };

    }

    return {
        // Get the Singleton instance if one exists
        // or create one if it doesn't
        getInstance: function () {
            if (!instance) {
                instance = init();
            }
            return instance;
        }
    };

})();
var RISKGauge = (function () {

    'use strict';
    var instance;

    function init() {

        var options = {
            tooltip: {
                // Function setNewData() (see below) already derived the tooltip, just return it:
                formatter: function(params, ticket, callback) {
                    return params.data.tooltip;
                },
                // The tooltip's default position is to the left of the cursor, which sends it off the screen.
                // So render it a little bit to the right of its default position:
                position : function(p) {
                    return [p[0] + 10, p[1] - 10];
                }
            },
            series: [
                {
                    pointer : {
                        width : 8,
                        length: '85%',
                        color: 'black'
                    },
                    name: 'Risky',
                    type: 'gauge',
                    splitNumber: 10,
                    axisLine: {
                        lineStyle: {
                            // rgba green value matches the olive green of the other charts
                            color: [[0.3, 'rgba(120,178,88,0.5)'], [0.8, 'rgba(255, 150, 0, 0.5)'], [1, 'rgba(255, 69, 0, 0.5)']],
                            width: 200
                        }
                    },
                    axisTick: {
                        splitNumber: 2,
                        length: 10,
                        lineStyle: {
                            color: 'black'
                        }
                    },
                    axisLabel: {
                        formatter: function (v) {
                            switch (v + '') {
                                case '10':
                                    return 'ok';
                                case '50':
                                    return 'warn';
                                case '90':
                                    return 'limit';
                                default:
                                    return '';
                            }
                        },
                        textStyle: {color: 'black'}
                    },
                    splitLine: {
                        show: true,
                        length: 20,
                        lineStyle: {
                            color: 'black'
                        }
                    },
                    title: {
                        show: true,
                        offsetCenter: [0, '-40%'],
                        textStyle: {
                            fontWeight: 'bolder'
                        }
                    },
                    detail: {
                        offsetCenter: ['-5%', 95],
                        formatter: function(value) {

                            // If the server returned a consumption value of NaN,
                            // then echarts does not render a needle.
                            // Set the legend text to 'NaN', echarts renders this in red.
                            if (Number.isNaN(value))
                                return 'NaN';

                            // If the consumption exceeds 100%, set the legend text to a warning message.
                            // Echarts renders this in red.
                            if (value > 100)
                                return 'LIMIT BREACH';

                            // Otherwise return the consumption value formatted as a percentage.
                            return numeral(value).format('(0.00)') + '%';
                        },
                        textStyle: {
                            color: 'auto',
                            fontWeight: 'bolder',
                            fontSize: 20
                        }
                    },
                    // Default the gauge to zero with no "name" (caption).
                    // This should immediately get overwritten by the data returned from the rest call.
                    data: 0
                }
            ]
        };

        function getToolTip(metricName, metricValue, limit, consumption) {
            var symbol_ = chartManager.getBaseCcy();
            metricName = metricName.toUpperCase();
            metricValue = numeral(metricValue).format('(0,0)');
            limit = numeral(limit).format('(0,0)');
            consumption = numeral(consumption).format('(0,0.00)');
            return metricName + ': ' + symbol_ + ' ' + metricValue + '<br/>Limit: ' + symbol_ + ' '+ limit + '<br/>Consumption: ' + consumption + '%';
        }

        function setNewData(chart_, data_) {

            chart_.setOption({

                series: [{

                    // Here is the normal way to pass in the data:
                    //
                    //  data: xxx
                    //
                    // Alternatively, echarts lets you pass in a list of objects.
                    // This allows you to attach additional properties to the data,
                    // where they can be retrieved later by the tooltip formatter event.
                    // We are going to use an array of size one:

                    data: [ {
                        // This is the data that echarts will render, it must be called 'value':
                        value : data_.consumption,
                        // The string to display on the center of the gauge - must be called 'name':
                        name : data_.name,
                        // Derive the tooltip for use in the tooltip formatter event:
                        tooltip : getToolTip(data_.metric, data_.value, data_.limit, data_.consumption)
                    } ],
                }],
            });
        }

        function initialiseAllCharts() {

            var args = {
                mode: chartManager.getMode(),
                hierarchy: chartManager.getHierarchy(),
                node: chartManager.getNode(),
                xvaHierarchy: chartManager.getHierarchy(),
                xvaNode: chartManager.getNode()
            };

            var p_ = chartManager.getGraphData(args, 'ce', 'gauge');
            chartManager.initChart('risk_gauge', options, p_, setNewData, false);
        }

        function loadData(chart_, data_) {
            setNewData(chart_, data_);
        }

// expose a few methods and properties
        return {
            initAllCharts: initialiseAllCharts,
            setNewData: setNewData,
            loadData: loadData,
            getDefaults: options
        };

    }

    return {
        // Get the Singleton instance if one exists
        // or create one if it doesn't
        getInstance: function () {
            if (!instance) {
                instance = init();
            }
            return instance;
        }
    };

})();

