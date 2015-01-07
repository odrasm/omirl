/**
 * Created by p.campanella on 24/09/2014.
 */

var SensorTableController = (function() {
    function SensorTableController($scope, oConstantsService, $log, oStationsService, oDialogService, oChartService, $location, oTableService) {
        this.m_oScope = $scope;
        this.m_oConstantsService = oConstantsService;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oStationsService = oStationsService;
        this.m_oDialogService = oDialogService;
        this.m_oChartService = oChartService;
        this.m_oLocation = $location;
        this.m_oTableService = oTableService;
        this.m_bDowloadEnabled = false;

        this.m_bShowCancelNameFilter = false;
        this.m_bShowCancelCodeFilter = false;
        this.m_bShowCancelBasinFilter = false;
        this.m_bShowCancelDistrictFilter = false;
        this.m_bShowCancelMunicipalityFilter = false;
        this.m_bShowCancelAreaFilter = false;

        this.m_aoStations = [];
        this.m_aoTypes = [];
        this.m_oSelectedType = {};

        this.m_bSideBarCollapsed = true;

        this.m_bReverseOrder = false;
        this.m_sOrderBy = "name";


        var oControllerVar = this;

        this.m_oStationsService.getStationsTypes().success(function (data, status) {

            for (var iTypes = 0; iTypes<data.length; iTypes ++) {
                if (data[iTypes].code == "Pluvio") {
                    oControllerVar.m_oSelectedType = data[iTypes];
                    break;
                }
            }

            oControllerVar.m_aoTypes = data;




            // QUESTO CHIUDE LA BARRA DI NAVIGAZIONE VOLENDO
            var oElement = angular.element("#mapNavigation");

            if (oElement != null) {
                if (oElement.length>0) {
                    var iWidth = oElement[0].clientWidth;
                    oElement[0].style.left = "-" + iWidth + "px";
                }
            }

            oControllerVar.typeSelected();

        }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Sensors Items to add to the Menu');
        });

    }

    SensorTableController.prototype.getStationList = function (sPath) {

        return this.m_aoStations;
    }

    SensorTableController.prototype.typeSelected = function() {

        var oControllerVar = this;

        /*
        this.m_oStationsService.getSensorsTable(this.m_oSelectedType.code).success(function (data, status) {
            oControllerVar.m_aoStations = data.tableRows;
            var aoStations = oControllerVar.m_aoStations;

            angular.forEach(oControllerVar.m_aoStations, function(value, key) {
                var NameCode = value.stationCode + ' ' + value.name;
                aoStations[key].nameCode = NameCode;

                if (value.municipality == null) aoStations[key].municipality = "";
                if (value.basin == null) aoStations[key].basin = "";
                if (value.area == null) aoStations[key].area = "";
            });

            oControllerVar.m_bDowloadEnabled = true;
        }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Sensors Items to add to the Menu');
        });
        */

        this.m_aoStations = this.m_oStationsService.getSensorsTable(this.m_oSelectedType.code);

        var aoStations = oControllerVar.m_aoStations;

        angular.forEach(oControllerVar.m_aoStations, function(value, key) {

            if (value.municipality == null) aoStations[key].municipality = "";
            if (value.basin == null) aoStations[key].basin = "";
            if (value.area == null) aoStations[key].area = "";
        });

        oControllerVar.m_bDowloadEnabled = true;
    }

    SensorTableController.prototype.exportCsv = function() {
        window.open(this.m_oStationsService.exportCsvStationList(this.m_oSelectedType.code), '_blank', '');
    }

    SensorTableController.prototype.stationClicked = function(sCode, sMunicipality, sName) {

        var sStationCode = sCode;

        var oControllerVar = this;

        var sTitle ="";

        if (angular.isDefined(sMunicipality) == false) {
            sMunicipality = "";
        }

        if (sMunicipality == null) sMunicipality = "";

        sTitle = sName + " (Comune di " + sMunicipality + ")";

        if (this.m_oDialogService.isExistingDialog(sStationCode)) {
            return;
        }

        var sSensorType = this.m_oSelectedType.code;

        // The data for the dialog
        var model = {
            "stationCode": sStationCode,
            "chartType": sSensorType,
            "municipality": sMunicipality,
            "name": sName
        };


        // jQuery UI dialog options
        var options = {
            autoOpen: false,
            modal: false,
            width: 600,
            resizable: false,
            close: function(event, ui) {
                // Remove the chart from the Chart Service
                oControllerVar.m_oChartService.removeChart(sStationCode);
            },
            title: sTitle
        };

        this.m_oDialogService.open(sStationCode,"stationsChart.html", model, options)
    }

    SensorTableController.prototype.CancelAllFilters = function() {
        this.CancelNameFilter();
        this.CancelCodeFilter();
        this.CancelDistrictFilter();
        this.CancelAreaFilter();
        this.CancelBasinFilter();
        this.CancelMunicipalityFilter();
    }

    SensorTableController.prototype.NameChanged = function(sNameFilter)
    {
        if (sNameFilter == "") this.m_bShowCancelNameFilter = false;
        else this.m_bShowCancelNameFilter = true;
    }

    SensorTableController.prototype.CancelNameFilter = function()
    {
        this.m_oScope.search.name="";
        this.m_bShowCancelNameFilter = false;
    }


    SensorTableController.prototype.CodeChanged = function(sCodeFilter)
    {
        if (sCodeFilter == "") this.m_bShowCancelCodeFilter = false;
        else this.m_bShowCancelCodeFilter = true;
    }

    SensorTableController.prototype.CancelCodeFilter = function()
    {
        this.m_oScope.search.stationCode="";
        this.m_bShowCancelCodeFilter = false;
    }
    SensorTableController.prototype.AreaChanged = function(sAreaFilter)
    {
        if (sAreaFilter == "") this.m_bShowCancelAreaFilter = false;
        else this.m_bShowCancelAreaFilter = true;
    }

    SensorTableController.prototype.CancelAreaFilter = function()
    {
        this.m_oScope.search.area="";
        this.m_bShowCancelAreaFilter = false;
    }

    SensorTableController.prototype.DistrictChanged = function(sFilter)
    {
        if (sFilter == "") this.m_bShowCancelDistrictFilter = false;
        else this.m_bShowCancelDistrictFilter = true;
    }

    SensorTableController.prototype.CancelDistrictFilter = function()
    {
        this.m_oScope.search.district="";
        this.m_bShowCancelDistrictFilter = false;
    }


    SensorTableController.prototype.BasinChanged = function(sFilter)
    {
        if (sFilter == "") this.m_bShowCancelBasinFilter = false;
        else this.m_bShowCancelBasinFilter = true;
    }

    SensorTableController.prototype.CancelBasinFilter = function()
    {
        this.m_oScope.search.basin="";
        this.m_bShowCancelBasinFilter = false;
    }

    SensorTableController.prototype.MunicipalityChanged = function(sFilter)
    {
        if (sFilter == "") this.m_bShowCancelMunicipalityFilter = false;
        else this.m_bShowCancelMunicipalityFilter = true;
    }

    SensorTableController.prototype.CancelMunicipalityFilter = function()
    {
        this.m_oScope.search.municipality="";
        this.m_bShowCancelMunicipalityFilter = false;
    }



    SensorTableController.prototype.toggleSideBarClicked = function() {

        var oElement = angular.element("#mapNavigation");

        if (oElement != null) {
            if (oElement.length>0) {
                var iWidth = oElement[0].clientWidth;
                iWidth -= 0;

                if (!this.m_bSideBarCollapsed) {
                    oElement[0].style.left = "-" + iWidth + "px";
                }
                else {
                    oElement[0].style.left =  "0px";
                }

                //oElement.sty
            }
        }

        this.m_bSideBarCollapsed = !this.m_bSideBarCollapsed;
    }

    SensorTableController.prototype.isSideBarCollapsed = function () {
        return this.m_bSideBarCollapsed;
    }

    SensorTableController.prototype.getTableLinks = function () {
        return this.m_aoTableLinks;
    }

    SensorTableController.prototype.linkClicked = function (sPath) {
        this.m_oLocation.path(sPath);
    }


    SensorTableController.$inject = [
        '$scope',
        'ConstantsService',
        '$log',
        'StationsService',
        'dialogService',
        'ChartService',
        '$location',
        'TableService'
    ];
    return SensorTableController;
}) ();
