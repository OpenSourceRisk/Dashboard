
    $(document).ready(function() {

        $("#glossaryModal").load("./glossary/modal.html"); 

        $(document).on('shown.bs.modal', '#glossary', function(e){
            var modal1=$('#glossary');
            $(modal1).find('#valuation_adjustments').load('./glossary/valuation_adjustments.html');
            $(modal1).find('#credit_risk_metrics').load('./glossary/credit_risk_metrics.html');
            $(modal1).find('#liquidity_risk_metrics').load('./glossary/liquidity_risk_metrics.html');
            $(modal1).modal();
        });
    });

