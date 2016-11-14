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

"use strict";

var donutZoom = function(graphId, xvaHierarchy, xvaNode) {

    var args = {
        mode: chartManager.getMode(),
        xvaHierarchy: xvaHierarchy,
        xvaNode: xvaNode
    };

    var p_ = chartManager.getGraphData(args, graphId, 'xva');
    chartManager.initChart('donut_xva', DONUTCharts.getInstance().getDefaults, p_, DONUTCharts.getInstance().setNewData, false);

    var parentHeader = document.getElementsByName('donut_' + graphId)[0];
    document.getElementById('myModalLabel2').innerText = parentHeader.innerText;
}

var load_ = function() {
    // load the page
    // build business date list
    // initialise all the charts
    // attach change event handlers to the dropdown controls
    console.log('[INFO] ORE Dashboard.init');

    // turn the limit toggle into a switch control
    var elem = document.getElementById('limitToggle');
    var init = new Switchery(elem, {size: 'small', color:'#73879C'});
    // add a click event handler for the button to show/hide limits.
    _AttachEvent(elem, 'change', chartManager.toggleLimitsClick);

    var pDates_ = chartManager.populateBusinessDates();
    var pCcy_ = chartManager.setBaseCcy();

    return Promise.all([pDates_, pCcy_]).then(function(values) {
        console.log('[INFO] bus date calls and base ccy complete');
        // when the bus date and base ccy rest calls have resolved
        // reset the page defaults and init the charts
        chartManager.resetPageDefaults();
        console.debug(sessionStorage);
        return chartManager.initAllCharts();
        // return 'done';
    }).then(function(res) {
        console.log('[INFO] init charts complete');
        // add a change event handler to the business date dropdown
        var nodes = document.getElementsByClassName('selectpicker');
        [].forEach.call(nodes, function(e) {
            _AttachEvent(e, 'change', chartManager.changeDate);
        });

        // for each bar graph selector, populate the choices listz
        // and add a change event handler
        [].forEach.call(document.getElementsByClassName('selectpicker-bg'), function(e) {
            chartManager.populateBarGraphMetricList(e);
            _AttachEvent(e, 'change', chartManager.setBGMetric);
        });

        var e = document.getElementById('gauge1_');
        chartManager.populateRiskGaugeMetricListImpl(e);
        _AttachEvent(e, 'change', chartManager.setRiskGaugeMetric);

        // add a click event handler to the radio buttons for credit rating/counterparty etc
        [].forEach.call($('label[name^="option"]'), function(e) {
            _AttachEvent(e, 'click', chartManager.drilldownMenuClick);
        });

        // add a click event handler to the breadcrumb for credit rating/counterparty etc
        [].forEach.call($('ol[id^="periscope"]'), function(e) {
            _AttachEvent(e, 'click', chartManager.breadcrumbClick);
        });

        // function to zoom a xva graph
        $('#xva-zoom').on('shown.bs.modal', function(e) {

            var graphId = $(e.relatedTarget).data('id');

            // Determine what parameters to pass to donutZoom().
            if (1 == chartManager.getMode()) {
                // HIERARCHY MODE: if the chosen hierarchy is trade then use nettingset instead.
                var level = Math.min(chartManager.getLevel(), 2);
                var xvaHierarchy = drilldownLevels[level].name;
                donutZoom(graphId, xvaHierarchy, '');
            } else {
                // TREE MODE: retrieve the stack, if we are below counterparty then use the (grand)parent counterparty.
                // NB: This copies some logic that is also implemented in fuction drillDown() - maybe consolidate?
                var url_ = '/api/periscope/' + chartManager.getHierarchy()  + '/' + chartManager.getNode();
                chartManager.getDataFromRestCall(url_).then(function(res) {
                    // Donut graphs don't go below nettingset level (i.e. displaying the children of a counterparty).
                    if (res.length > 1) {
                        // Set the donut graph hierarchy to "counterparty":
                        var xvaHierarchy = res[1].hierarchy;
                        // Set the donut graph node to whichever counterparty is the (grand)parent of the selected node:
                        var xvaNode = res[1].item;
                    } else {
                        // We are at nettingset level or above, so the donut graphs just display the selected node:
                        var xvaHierarchy = chartManager.getHierarchy();
                        var xvaNode = chartManager.getNode();
                    }
                    donutZoom(graphId, xvaHierarchy, xvaNode);
                });
            }
        });
        return 'done';

    }).then(function(res) {
        chartManager.setBGMetricDefaults();
        console.log('[INFO] ORE Dashboard init completed');
        return 'done';
    }).catch(function(error) {
            console.error(new Error(error));
    });
}

var drilldownLevels = [
    {name: 'creditrating', level: 0, text: 'Credit Rating'},
    {name: 'counterparty', level: 1, text: 'Counterparty'},
    {name: 'nettingset', level:2, text:'Netting Set'},
    {name: 'trade', level:3, text: 'Trade'}
];

var currencyMap = [
    {ccy: 'BRL', symbol: 'R$'},
    {ccy: 'CNY', symbol: '¥'},
    {ccy: 'CZK', symbol: 'Kč'},
    {ccy: 'DKK', symbol: 'kr'},
    {ccy: 'EUR', symbol: '€'},
    {ccy: 'HUF', symbol: 'Ft'},
    {ccy: 'ISK', symbol: 'kr'},
    {ccy: 'IDR', symbol: 'Rp'},
    {ccy: 'JPY', symbol: '¥'},
    {ccy: 'KRW', symbol: '₩'},
    {ccy: 'NOK', symbol: 'kr'},
    {ccy: 'RUB', symbol: 'руб'},
    {ccy: 'SEK', symbol: 'kr'},
    {ccy: 'CHF', symbol: 'CHF'},
    {ccy: 'GBP', symbol: '£'},
    {ccy: 'USD', symbol: '$'}
];

var chartCategory = [
    {metric: 'npv', category: 'MARKET', tradeLevel:true},
    {metric: 'ce', category: 'CREDIT', tradeLevel:true},
    {metric: 'epe', category: 'CREDIT', tradeLevel:true},
    {metric: 'ene', category: 'CREDIT', tradeLevel:true},
    {metric: 'pfe', category: 'CREDIT', tradeLevel:true},
    {metric: 'eepe', category: 'CREDIT', tradeLevel:true},
    //{metric: 'totalexposure', category: 'CREDIT', tradeLevel:true},
    {metric: 'cva', category: 'CREDIT', tradeLevel:false},
    {metric: 'dva', category: 'CREDIT', tradeLevel:false},
    {metric: 'saccr', category: 'CREDIT', tradeLevel:true},
    {metric: 'el', category: 'CREDIT', tradeLevel:true},
    {metric: 'uel', category: 'CREDIT', tradeLevel:true},
    {metric: 'var', category: 'MARKET', tradeLevel:true},
    {metric: 'es', category: 'MARKET', tradeLevel:true},
    {metric: 'fca', category: 'LIQUIDITY', tradeLevel:false},
    {metric: 'fba', category: 'LIQUIDITY', tradeLevel:false},
    {metric: 'fva', category: 'LIQUIDITY', tradeLevel:false},
    {metric: 'colva', category: 'LIQUIDITY', tradeLevel:false},
    {metric: 'mva', category: 'LIQUIDITY', tradeLevel:false},
    //{metric: 'im', category: 'LIQUIDITY', tradeLevel:true},
    {metric: 'vm', category: 'LIQUIDITY', tradeLevel:true},
    {metric: 'snco', category: 'LIQUIDITY', tradeLevel:true},
    {metric: 'rsf', category: 'LIQUIDITY', tradeLevel:true}
];

var barGraphs = [
    {id: 'bar_1', name: 'bar1', metric: 'ce', text: ''},
    {id: 'bar_2', name: 'bar2', metric: 'npv', text: ''},
    {id: 'bar_3', name: 'bar3', metric: 'fca', text: ''},
    {id: 'bar_4', name: 'bar4', metric: 'fba', text: ''},
    {id: 'bar_5', name: 'bar5', metric: 'eepe', text: ''},
    {id: 'bar_6', name: 'bar6', metric: 'cva', text: ''}
];

// deep copy clone
var userBarGraphs = JSON.parse(JSON.stringify(barGraphs));

var xvaGraphs = [
    {id: 'donut_cva', name: 'donut_cva', metric: 'cva', text: 'CVA'},
    {id: 'donut_fva', name: 'donut_fva', metric: 'fva', text: 'FVA'},
    {id: 'donut_colva', name: 'donut_colva', metric: 'colva', text: 'ColVA'}
];

var chartManager = {

    initAllCharts: function() {
        LINECharts.getInstance().initAllCharts();
        BARCharts.getInstance().initAllCharts();
        DONUTCharts.getInstance().initAllCharts();
        RISKGauge.getInstance().initAllCharts();
        return Promise.resolve('done');
    }
    , initChart: function(chartTagName_, options, data_, fnLoadData_, clickable) {
        // once data is resolved, render it
        return Promise.resolve(data_).then(function(res){
            var theChart_ = echarts.init(document.getElementById(chartTagName_), theme);
            theChart_.setOption(options);
            if (clickable)
                theChart_.on('click', chartManager.drilldownChartClick);
            fnLoadData_(theChart_, res);
            return ['done', 'initChart', chartTagName_];
        });
    },
    getChartInstanceFromDivId: function (chartTagName_) {
        return echarts.getInstanceByDom(document.getElementById(chartTagName_));
    },
    getDataFromRestCall: function(url_) {

        var req_ = new Request({
                headers: {
                    'Cache-Control': 'no-cache',
                    'If-Modified-Since': '0',
                    'Accept': 'application/json'
                },
                method: 'GET',
                mode: 'cors',
                credentials: 'same-origin'
            }
        );

        var theUrl_ = window.location.protocol + '//' + window.location.host + url_;
        console.log(theUrl_);

        return fetch(
            theUrl_, req_)
            .then(processStatus)
            .then(parseJson)
            .then(function(response) {
                // console.debug(response);
                return response;
            })
            .catch(function(ex) {
                StackTrace.fromError(ex)
                    .then(console.error);
                return Promise.reject(ex);
            })
    }
    , toggleLimitsClick : function(evt) {

        if (isNullOrUndefined(evt))
            return;

        evt = evt || window.event;
        var target = evt.target || evt.srcElement;

        // flip the hide/show limits flag.
        var onOrOff = target.checked ? 1:0;
        chartManager.toggleLimits(onOrOff);

        // rerender the dashboard with the new limits flag
        // without changing anything else.
        var args = {
            mode: chartManager.getMode(),
            level: chartManager.getLevel(),
            node: chartManager.getNode()
        };
        chartManager.drillDown(args);
    }
    , setBaseCcy: function() {
        return chartManager.getDataFromRestCall('/api/baseccy')
            .then(function(response) {
                sessionStorage.setItem('baseccy', response || 'USD');
                return response;
            })
    }
    , getBaseCcy: function() {
        var ccy_ = sessionStorage.getItem('baseccy');
        if (isNullOrUndefined(ccy_)){
            ccy_ = Promise.resolve(chartManager.setBaseCcy());
        }
        var symbol_ = filter(currencyMap, function(elem){return elem.ccy === ccy_;})[0].symbol;
        return symbol_;
    }
    , populateBusinessDates: function() {
        try {
            var sel = document.getElementById('businessDates');
            // zero out the existing options
            sel.options.length = 0;

            var fragment = document.createDocumentFragment();
            return chartManager.getDataFromRestCall('/api/dates')
                .then(function(response) {
                    var opt = new Option();
                    opt.innerHTML = '- Date -';
                    opt.value = '19700101';
                    fragment.appendChild(opt);

                    response.forEach(function(dcc, index) {
                        var opt = document.createElement('option');
                        // nice format for the user to see
                        opt.innerHTML = moment(dcc, 'YYYYMMDD').format('DD-MMM-YYYY');
                        // nice format for the computer to see
                        opt.value = dcc;
                        fragment.appendChild(opt);
                    });
                    sel.appendChild(fragment);

                    sessionStorage.setItem('businessDate', businessDates.options[0].value );
                    return 'done';
                }).catch(function(e) {
                    return Promise.reject(new Error(e));
                })
        } catch (e) {
            console.error(new Error(e));
        }
    }
    , populateBarGraphMetricList : function(element) {
        try {
            var sel = element;
            // zero out the existing options
            sel.options.length = 0;
            var fragment = document.createDocumentFragment();

            ['CE','EEPE','CVA','DVA','NPV','FCA','FBA','FVA','ColVA'].forEach(function(dcc, index) {
                var opt = document.createElement('option');
                // nice format for the user to see
                opt.innerHTML = dcc;
                // nice format for the computer to see
                opt.value = dcc;
                fragment.appendChild(opt);
            });
            sel.appendChild(fragment);
        } catch (e) {
            console.error(new Error(e));
        }
    }
    , populateRiskGaugeMetricList : function() {
        var e = document.getElementById('gauge1_');
        chartManager.populateRiskGaugeMetricListImpl(e);
    }
    , populateRiskGaugeMetricListImpl : function(element) {
        try {
            // zero out the existing options
            element.options.length = 0;
            var fragment = document.createDocumentFragment();

            // Restrict the Risk Gauge drop down to the list of options for which
            // both Metric and Limit are available given the selected mode / level.
            if (2==chartManager.getMode() && 3==chartManager.getLevel())
                // If we are in tree view, and looking at a trade, then only CE and EEPE are available:
                var choiceList = ['CE','EEPE'/*,'CVA','DVA','NPV','FCA','FBA','FVA','ColVA'*/];
            else
                // In all other cases (hierarchy view, or other tree views) we can see everything except NPV and ColVA:
                var choiceList = ['CE','EEPE','CVA','DVA'/*,'NPV'*/,'FCA','FBA','FVA'/*,'ColVA'*/];

            // If possible, preserve the currently selected metric.
            var previouslySelectedMetric = chartManager.getRiskGaugeMetric().toUpperCase();
            var metricReused = false;

            choiceList.forEach(function(dcc, index) {
                var opt = document.createElement('option');
                // nice format for the user to see
                opt.innerHTML = dcc;
                // nice format for the computer to see
                opt.value = dcc;

                // If the current item matches the previously selected one then select it.
                if (!metricReused && dcc === previouslySelectedMetric) {
                    opt.selected = true;
                    metricReused = true;
                }

                fragment.appendChild(opt);
            });
            element.appendChild(fragment);

            // If the previous selection could not be kept then reset to CE.
            if (!metricReused)
                sessionStorage.setItem('gauge_metric', 'ce');
        } catch (e) {
            console.error(new Error(e));
        }
    }
    , flipGauge : function(evt) {
        if (isNullOrUndefined(evt))
            return;

        evt = evt || window.event;
        var target = evt.target || evt.srcElement;

        // refresh the single chart with the right metric
        // redraw the gauge with a new metric
        var gaugeInstance = RISKGauge.getInstance();
        var metric_ = chartManager.getRiskGaugeMetric();

        var args = {
            mode: chartManager.getMode(),
            hierarchy: chartManager.getHierarchy(),
            node: chartManager.getNode()
        };

        var p_ = chartManager.getGraphData(args, metric_, 'gauge');
        p_.then(function(res) {
            chartManager.initChart('risk_gauge', gaugeInstance.getDefaults, p_, gaugeInstance.setNewData, false);
        }).catch(function(err) {
            console.error(new Error(err));
        });
    },
    setBarGraphTitle : function(graphId_, cat_){
        var instance_ = chartManager.getChartInstanceFromDivId(graphId_);
        var titleText_ = '';
        if (chartManager.getLevel() ==3 && cat_[0].tradeLevel == false)
            titleText_ = 'Not Applicable at Trade Level';

        instance_.setOption({title: [{text: titleText_}]});
    }, flipChart : function(evt) {
        if (isNullOrUndefined(evt))
            return;

        evt = evt || window.event;
        var target = evt.target || evt.srcElement;

        // refresh the single chart with the right metric
        // redraw the barGraph with a new metric
        var bgInstance = BARCharts.getInstance();
        var graphId_ = filter(barGraphs, function(elem){return elem.name == target.name})[0].id;
        var metric_ = chartManager.getBarGraphMetric(target.name);

        var args = {
            mode: chartManager.getMode(),
            hierarchy: chartManager.getHierarchy(),
            node: chartManager.getNode()
        };

        var p_ = chartManager.getGraphData(args, metric_, 'bargraph');
        p_.then(function(res) {
            chartManager.initChart(graphId_, bgInstance.getDefaults, p_, bgInstance.setNewData, chartManager.barGraphIsClickable());
            return 'done';
        }).then(function(res){
            // lookup the category in the chartCategories array
            var cat_ = filter(chartCategory, function (elem) {
                return elem.metric == metric_;
            });
            document.getElementById(graphId_).parentNode.parentNode.getElementsByTagName('h2')[0].innerText = cat_[0].category;
            chartManager.setBarGraphTitle(graphId_, cat_);
        }).catch(function(err) {
            // FIXME if the chart flip returns no data or an error, revert the change
            // with a popup?
            console.error(new Error(err));
        });
    }
    , getGenericGraphData : function(args_) {
        console.debug(args_);
        if (1 == args_.mode) {
            // display all of the nodes in the chosen hierarchy
            if ('bargraph' === args_.chartType) {
                return '/api/bargraph/' + args_.date + '/' + args_.hierarchy + '/' + args_.metric;
            } else if ('xva' === args_.chartType) {
                return '/api/xva/' + args_.date + '/' + args_.xvaHierarchy + '/' + args_.metric;
            } else if ('exposure' === args_.chartType) {
                // exposure does not support hierarchy view, just display the root node.
                return '/api/exposure-tree/' + args_.date + '/total/Total';
            } else if ('totalexposure' === args_.chartType) {
                // totalexposure does not support hierarchy view, just display the root node.
                return '/api/totalexposure-tree/total/Total';
            } else if ('gauge' === args_.chartType) {
                // gauge does not support hierarchy view, just display the root node.
                return '/api/gauge-tree/' + args_.date + '/total/Total/' + args_.metric;
            } else {
                throw "Invalid chart type : " + args_.chartType;
            }
        } else if (2 == args_.mode) {
            // display all of the children of the selected node
            if ('bargraph' === args_.chartType) {
                return '/api/bargraph-tree/' + args_.date + '/' + args_.hierarchy + '/' + args_.node + "/" + args_.metric;
            } else if ('xva' === args_.chartType) {
                return '/api/xva-tree/' + args_.date + '/' + args_.xvaHierarchy + '/' + args_.xvaNode + "/" + args_.metric;
            } else if ('exposure' === args_.chartType) {
                return '/api/exposure-tree/' + args_.date + '/' + args_.hierarchy + '/' + args_.node;
            } else if ('totalexposure' === args_.chartType) {
                return '/api/totalexposure-tree/' + args_.hierarchy + '/' + args_.node;
            } else if ('gauge' === args_.chartType) {
                return '/api/gauge-tree/' + args_.date + '/' + args_.hierarchy + '/' + args_.node + "/" + args_.metric;
            } else {
                throw "Invalid chart type : " + args_.chartType;
            }
        } else {
            throw "Invalid mode : " + args_.mode;
        }
    }
    , getBusinessDate : function() {
        return sessionStorage.getItem('businessDate')/* || businessDate.value*/;
    }
    , getHierarchy : function() {
        return drilldownLevels[chartManager.getLevel()].name;
    }
    , setNode : function(i) {
        sessionStorage.setItem('node', i);
    }
    , getNode : function() {
        return sessionStorage.getItem('node');
    }
    , getBarGraphMetric : function(id_) {
        var default_ = filter(barGraphs, function(elem){return elem.name == id_});
        return (sessionStorage.getItem(id_) || default_[0].metric).toLowerCase();
    }
    , getRiskGaugeMetric : function() {
        return sessionStorage.getItem('gauge_metric');
    }
    , getGraphData : function(args, metric_, chartType_) {
        // deep copy
        //var localArgs = JSON.parse(JSON.stringify(args));
        var localArgs = args;
        localArgs.metric = metric_;
        localArgs.chartType = chartType_;
        localArgs.date = chartManager.getBusinessDate();

        var url_ = chartManager.getGenericGraphData(localArgs);
        // returns a promise (future)
        return chartManager.getDataFromRestCall(url_);
    }
    , refreshGraphs : function(args) {
        // console.debug(args);
        var bgInstance = BARCharts.getInstance();
        var xvInstance = DONUTCharts.getInstance();
        var lineInstance = LINECharts.getInstance();
        var gaugeInstance = RISKGauge.getInstance();

        barGraphs.forEach(function(elem) {
            var metric_ = chartManager.getBarGraphMetric(elem.name);
            var p_ = chartManager.getGraphData(args, metric_, 'bargraph');

            p_.then(function(res) {
                chartManager.initChart(elem.id, bgInstance.getDefaults, p_, bgInstance.setNewData, chartManager.barGraphIsClickable());
                return 'done';
            }).then(function(res){
                // lookup the category in the chartCategories array
                var cat_ = filter(chartCategory, function(elem){return elem.metric == metric_;});
                document.getElementById(elem.id).parentNode.parentNode.getElementsByTagName('h2')[0].innerText = cat_[0].category;
                chartManager.setBarGraphTitle(elem.id,cat_);
            });
        });

        xvaGraphs.forEach(function(elem) {
            var p_ = chartManager.getGraphData(args, elem.metric, 'xva');
            p_.then(function(res) {
                chartManager.initChart(elem.name, xvInstance.getDefaults, p_, xvInstance.setNewData, true);
                var titleText_ = elem.text + " : " + chartManager.getBaseCcy() + ' ' + numeral(res.sum).format('(0.00a)');
                document.getElementsByName(elem.name)[0].innerText = titleText_;
            });
        });

        var totexp_ = chartManager.getGraphData(args, '', 'totalexposure');
        totexp_.then(function(res) {
            chartManager.initChart('line_total_exposure', lineInstance.getDefaultTotalOptions, totexp_, lineInstance.setNewData, false);
        });

        var exp_ = chartManager.getGraphData(args, '', 'exposure');
        exp_.then(function(res) {
            chartManager.initChart('line_exposure_profile', lineInstance.getDefaultExposureOpts, exp_, lineInstance.setNewData, false);
        });

        var metric_ = chartManager.getRiskGaugeMetric();
        var gauge_ = chartManager.getGraphData(args, metric_, 'gauge');
        gauge_.then(function(res) {
            chartManager.initChart('risk_gauge', gaugeInstance.getDefaults, gauge_, gaugeInstance.setNewData, false);
        });

        return 'done';

    }
    , setBGMetricDefaults : function() {
        var nodes = document.getElementsByClassName('selectpicker-bg');
        // set initial values
        barGraphs.forEach(function(elem) {
            sessionStorage.setItem(elem.name, elem.metric.toUpperCase());
            filter(nodes,function(e){return e.name == elem.name})[0].value = elem.metric.toUpperCase();
        });
    }
    , resetPageDefaults : function() {
        // set the business date dropdown to item 1
        var nodes = document.getElementsByClassName('selectpicker');
        [].forEach.call(nodes,function(e) {
            e.selectedIndex = 1;
        });

        // set initial values
        chartManager.setMode(1);
        var bus_ = businessDates.options[businessDates.selectedIndex].value ;
        sessionStorage.setItem('businessDate', bus_);
        chartManager.setLevel(0);
        chartManager.setNode('');

        chartManager.setBGMetricDefaults();
        sessionStorage.setItem('gauge_metric', 'ce');
        sessionStorage.setItem('limits', 1);
    }
    // change of business date
    , changeDate : function(evt) {
        if (isNullOrUndefined(evt))
            return;

        // save the value selected in the dropdown.
        // at present the value in question is always the business date.
        evt = evt || window.event;
        var target = evt.target || evt.srcElement;
        sessionStorage.setItem(target.name, target.value);
        document.getElementById(target.name).value = target.value;

        // At present the test below is always true because this
        // function is only called for the business date dropdown.
        if (target.name == 'businessDate') {

            // rerender the dashboard with the new date
            // without changing the selected hierarchy.

            var args = {
                mode: chartManager.getMode(),
                level: chartManager.getLevel(),
                node: chartManager.getNode()
            };
            // Reload the page with the new business date.
            chartManager.drillDown(args);
        }
    }
    // Should the bar graphs be clickable?
    // If mode=1 (hierarchy) then the bar graphs are always clickable.
    // If mode=2 (tree) then the bar graphs are clickable as long as we are above trade level.
    , barGraphIsClickable : function() {
        return 1 == chartManager.getMode() || chartManager.getLevel() < 3;
    }
    , setBGMetric : function(evt) {
        if (isNullOrUndefined(evt))
            return;

        evt = evt || window.event;
        var target = evt.target || evt.srcElement;
        sessionStorage.setItem(target.name, target.value);
        chartManager.flipChart(evt);
        // set the userBG item
        var default_ = filter(userBarGraphs, function(elem){return elem.name == target.name});
        default_[0].metric = target.value.toLowerCase();
    }
    , setRiskGaugeMetric : function(evt) {
        if (isNullOrUndefined(evt))
            return;

        evt = evt || window.event;
        var target = evt.target || evt.srcElement;
        sessionStorage.setItem('gauge_metric', target.value.toLowerCase());
        chartManager.flipGauge(evt);
        //var default_ = filter(userRiskGauges, function(elem){return elem.name == target.name});
        //default_[0].metric = target.value.toLowerCase();
    }
    , setMode : function(m) {
        sessionStorage.setItem('mode', +m);
    }
    , getMode : function() {
        return +sessionStorage.getItem('mode');
    }
    , setLevel : function(e) {
        sessionStorage.setItem('level', +e);
    }
    , getLevel : function() {
        return +sessionStorage.getItem('level');
    }
    , setDrilldownMenu : function(level) {
        $('input:radio')[level].checked = true;
        $($('label[name^="option"]')[level]).button('toggle');
    }
    , toggleLimits : function(onOrOff) {
            sessionStorage.setItem('limits', onOrOff);
    }
    , getLimits : function() {
        return sessionStorage.getItem('limits');
    }
// Function drillDown:  Rerender the dashboard.
//                      Whether or not this actually drills down depends on whether the caller incremented the level.
//
//  var args = {
//      mode: xxx,          1=hierarchy (view all nodes in selected level) 2=tree (view children of selected node)
//      level: xxx,         The level of the hierarchy to be displayed.
//      node: xxx,          if mode=1, this value is ignored and should be set to '', if node=2 this is the selected node
//  };
    , drillDown : function(args) {

        // Save the new state.
        chartManager.setMode(args.mode);
        chartManager.setLevel(args.level);
        chartManager.setNode(args.node);

        // Repopulate the dropdown for the risk gauge.
        chartManager.populateRiskGaugeMetricList();

        if (1 == args.mode) {
            // Hierarchy view - display all nodes in selected level.
            args.hierarchy = chartManager.getHierarchy();
            // Donut graphs don't go below nettingset level.
            args.xvaHierarchy = 'trade' == args.hierarchy ? 'nettingset' : args.hierarchy;
            // Delete the breadcrumbs.
            chartManager.resetCrumbs();
            // Rerender the page.
            chartManager.refreshGraphs(args);
        } else {
            // Tree view - display all children of selected node.
            args.hierarchy = chartManager.getHierarchy();
            // Mark the corresponding radio button as pressed (hierarchy view).
            chartManager.setDrilldownMenu(args.level);
            // Retrieve the stack leading to the selected node.
            var url_ = '/api/periscope/' + args.hierarchy  + '/' + args.node;
            chartManager.getDataFromRestCall(url_).then(function(res) {
                // Donut graphs don't go below nettingset level.
                if (res.length > 1) {
                    // Set the donut graph hierarchy to "counterparty":
                    args.xvaHierarchy = res[1].hierarchy;
                    // Set the donut graph node to whichever counterparty is the parent of the selected node:
                    args.xvaNode = res[1].item;
                } else {
                    // We are at nettingset level or above, so the donut graphs just display the selected node:
                    args.xvaHierarchy = args.hierarchy;
                    args.xvaNode = args.node;
                }
                // Update the breadcrumbs.
                chartManager.setCrumbs(res);
                // Rerender the page.
                chartManager.refreshGraphs(args);
            });
        };
    }
    , resetCrumbs : function() {
        var bcList = document.getElementById('periscope');
        while (bcList.firstChild) {
            bcList.removeChild(bcList.firstChild);
        }
        var listItem = document.createElement('li');
        listItem.className="breadcrumb-item";
        var a = document.createElement('a');
        a.href="index.html";
        a.id = "crumb";
        a.setAttribute('data-hierarchy', 'total');
        a.setAttribute('data-item', 'Total');
        a.setAttribute('data-level', '0');
        var i = document.createElement('i');
        i.className="fa fa-home";
        a.appendChild(i);
        listItem.appendChild(a);
        bcList.appendChild(listItem);
    }
    , setCrumbs : function(breadcrumbStack_) {
        // set the breadcrumbs according to the response from the JSON message
        // in periscope rest endpoint
        chartManager.resetCrumbs();
        var bcList = document.getElementById('periscope');

        [].forEach.call(breadcrumbStack_, function(e) {
            var listItem = document.createElement('li');
            listItem.className="breadcrumb-item";
            var a = document.createElement('a');
            a.href="#";
            a.id = "crumb" +e.level;
            a.setAttribute('data-hierarchy', e.hierarchy);
            a.setAttribute('data-item', e.item);
            a.setAttribute('data-level', e.level);
            a.innerText = e.item;
            listItem.appendChild(a);
            bcList.appendChild(listItem);
        });
    }
    , breadcrumbClick : function(evt) {
        // user clicked on the menu
        if (isNullOrUndefined(evt))
            return;

        evt = evt || window.event;
        var target = evt.target || evt.srcElement;

        var mode = 2;
        var node = target.getAttribute('data-item');
        var level = Math.min(+target.getAttribute('data-level'), 3);
        //var hierarchy = target.getAttribute('data-hierarchy');

        var args = {
            mode: mode,
            level: level,
            node: node
        };
        chartManager.drillDown(args);
    }
    , drilldownMenuClick : function(evt) {
        // user clicked on the menu
        if (isNullOrUndefined(evt))
            return;

        evt = evt || window.event;
        var target = evt.target || evt.srcElement;

        var args = {
            mode: 1,
            level: target.children[0].value,
            node: ''
        };
        chartManager.drillDown(args);
    }
    , drilldownChartClick : function(evt) {
        // user clicked on the chart
        if (isNullOrUndefined(evt))
            return;

        evt = evt || window.event;
        console.log(evt);

        // Determine the level for the new render.
        if (1 == chartManager.getMode())
            // Changing from hierarchy to tree.  Don't drill down.
            var level = chartManager.getLevel();
        else
            // Already in tree, drill down to next level.
            var level = chartManager.getLevel() + 1;
        // If the graph that got clicked on was a donut then don't go below nettingset level.
        if (evt.seriesType === 'pie')
            level = Math.min(level, 2);

        var args = {
            mode: 2,
            level: level,
            node: evt.name
        };
        chartManager.drillDown(args);
    }
}

// StackTrace.instrument(chartManager.drillDown, StackTraceCallback, StackTraceErrback);

var StackTraceCallback = function(stackframes) {
    var stringifiedStack = stackframes.map(function(sf) {
        return sf.toString();
    }).join('\n');
    console.log(stringifiedStack);
};

var StackTraceErrback = function(err) { console.log(err.message); };

