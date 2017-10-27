/**
 * Created by poovarasanv on 19/10/17.
 */

$(function () {

    $('#uploadDiv').hide();

    var socket = io(window.location.hostname + ':' + SOCKETPORT, {
        transports: ['websocket', 'polling']
    });
    socket.on('connect', function () {
        console.log("Socket Connected")
    });
    socket.on('logger', function (data) {
        console.log("Log :" + data)
        $('#loggerArea').append(data);

        $('#loggerArea').scrollTop = $('#loggerArea').scrollHeight;
    });

    socket.on('error', function (data) {
        console.log("Log :" + data)
        $('.errorlog').append(data);
    });

    socket.on('excelprogress', function (data) {
        $('#progress').html("Progress : " + data);
    });

    socket.on('rolelog', function (data) {
        $('#loggerArea').append(data);
    });
    socket.on('disconnect', function (reason) {
        console.log("Socket Disconnected : because " + reason)
    });


    $('#categoryLoader').hide();
    $('#templateLoader').hide();

    $('#categoryDropDown').attr("disabled", "disabled")
    $('#templates').attr("disabled", "disabled")

    $('#env').on('change', function () {
        var selectedenv = $('#env').val();

        $('#categoryLoader').show();

        if (selectedenv != "") {
            $.ajax({
                url: CONTEXTPATH + '/api/category',
                data: {"env": selectedenv},
                success: function (result) {
                    $('#categoryDropDown option').remove();
                    $('#categoryDropDown').append("<option value=''>Select Category</option>");

                    $.each(result, function (i, o) {
                        $('#categoryDropDown').append("<option value='" + o.categoryName + "'>" + o.categoryName + "</option>")
                    })
                    $('#categoryDropDown').removeAttr("disabled")
                    $('#categoryLoader').hide();

                }
            })
        }
    });

    $('#categoryDropDown').on('change', function () {
        var selectedRoleCategory = $('#categoryDropDown').val();
        var selectedenv = $('#env').val();

        $('#templateLoader').show();
        if (selectedRoleCategory != "") {
            $.ajax({
                url: CONTEXTPATH + '/api/surveyTemplate',
                data: {"category": selectedRoleCategory, "env": selectedenv},
                success: function (result) {
                    $('#templates option').remove();
                    $('#templates').append("<option value=''>Select Service Template</option>");

                    $.each(result, function (i, o) {
                        $('#templates').append("<option value='" + o.templateInstanceId + "'>" + o.surveyTemplateName + "</option>")
                    })
                    $('#templates').removeAttr("disabled")
                    $('#templateLoader').hide();
                }
            })
        }
    });

    var selectedItems = [];

    $("#uploadForm").on("submit", function (e) {
        e.preventDefault();
        $('.errorlog').html("");
        $('#js-grid-div').html("");
        $('#js-grid-role-detail').html("");
        $('#js-grid-div').jsGrid({
            height: "420px",
            width: "100%",
            noDataContent: "No Records Found",
            sorting: true,
            paging: false,
            autoload: true,
            editing: true,
            inserting: true,
            selecting: true,
            deleteConfirm: function (item) {
                return "The Role \"" + item.Role_x + "\" will be removed. Are you sure?";
            },
            rowClick: function (args) {

                var $row = this.rowByItem(args.item);
                $row.toggleClass("highlight");

                var answerIndex = args.item.costcode;

                $('#selectedRoleLabel').html(args.item.id + " : " + args.item.rolex);


                $('#js-grid-role-detail').html("");
                $('#js-grid-role-detail').jsGrid({

                    height: "280px",
                    width: "100%",
                    sorting: true,
                    paging: false,
                    autoload: true,
                    noDataContent: "No Records Found",
                    controller: {
                        loadData: function () {

                            var d = $.Deferred();
                            var selectedenv = $('#env').val();
                            $.ajax({
                                url: CONTEXTPATH + '/api/roleDetail',
                                data: {"index": answerIndex, "env": selectedenv},
                                type: 'GET',
                                success: function (data) {
                                    $('#totalDetailCount').html("<lable>Total Records : " + data.length + "</label>")
                                    console.log(data);
                                    d.resolve(data);
                                },
                                error: function (error) {
                                    console.log(error)
                                }
                            });
                            return d.promise();
                        }
                    },
                    fields: [

                        {title: "Question", type: "text", name: "question", headercss: "header-color"},
                        {title: "Answer", type: "text", name: "answerValue", headercss: "header-color"},
                        {title: "Question Id", type: "text", name: "questionId", headercss: "header-color"}
                    ]
                })


            },


            controller: {
                insertItem: function (item) {
                    var d = $.Deferred();
                    $.ajax({
                        url: CONTEXTPATH + "/api/addRole",
                        type: "POST",
                        data: item
                    }).done(function (updatedItem) {
                        d.resolve(updatedItem)
                    });
                    return d.promise();
                },

                updateItem: function (item) {

                    var d = $.Deferred();
                    $.ajax({
                        url: CONTEXTPATH + "/api/updateRole",
                        type: "POST",
                        data: item
                    }).done(function (updatedItem) {
                        d.resolve(updatedItem)
                    });
                    return d.promise();
                },


                loadData: function () {
                    var d = $.Deferred();
                    var selectedenv = $('#env').val();
                    var formData = new FormData();
                    formData.append('file', $('#excelFile')[0].files[0]);
                    formData.append('answer_start_index', $('#answer_start_index').val());
                    formData.append('templates', $('#templates').val());
                    formData.append('category', $('#categoryDropDown').val());
                    formData.append('env', selectedenv);

                    $.ajax({
                        url: CONTEXTPATH + '/api/parseExcel',
                        type: 'POST',
                        data: formData,
                        processData: false,  // tell jQuery not to process the data
                        contentType: false,  // tell jQuery not to set contentType
                        success: function (data) {

                            $('#totalCount').html("<lable>Total Records : " + data.length + "</label>")
                            console.log(data);
                            $('#uploadDiv').show();
                            d.resolve(data);
                        }
                    });
                    return d.promise();
                }
            },

            fields: [
                // {
                //     headerTemplate: function () {
                //         return $("<button>").attr("type", "button")
                //             .attr('class', 'btn btn-success').text("Add")
                //             .on("click", function () {
                //                 deleteSelectedItems();
                //             });
                //     },
                //     itemTemplate: function (_, item) {
                //         return $("<input>").attr("type", "checkbox")
                //             .prop("checked", $.inArray(item, selectedItems) > -1)
                //             .on("change", function () {
                //                 $(this).is(":checked") ? selectItem(item) : unselectItem(item);
                //             });
                //     },
                //     align: "center",
                //     width: 50
                // },
                {
                    name: "id",
                    type: "number",
                    width: 30,
                    headercss: "header-color",
                    editable: false,
                    validate: "required"
                },
                {name: "rolex", type: "text", headercss: "header-color", title: "Roles", validate: "required"},
                {
                    name: "roleprefix",
                    type: "text",
                    headercss: "header-color",
                    title: "Role Prefix",
                    validate: "required"
                },
                {name: "costcode", type: "text", headercss: "header-color", title: "Cost Code", validate: "required"},
                {type: "control", title: "Edit / Delete", deleteButton: false}
            ]
        });
    });


    var selectItem = function (item) {
        selectedItems.push(item);
        console.log(selectedItems)
    };

    var unselectItem = function (item) {
        selectedItems = $.grep(selectedItems, function (i) {
            return i !== item;
        });
        console.log(selectedItems)
    };

    var deleteSelectedItems = function () {
        if (!selectedItems.length || !confirm("Are you sure?"))
            return;


        // var deleteClientsFromDb = function(deletingClients) {
        //     db.clients = $.map(db.clients, function(client) {
        //         return ($.inArray(client, deletingClients) > -1) ? null : client;
        //     });
        // };
        //deleteClientsFromDb(selectedItems);

        var $grid = $("#jsGrid");
        $grid.jsGrid("option", "pageIndex", 1);
        $grid.jsGrid("loadData");

        selectedItems = [];
    };

    $("#upload").on('click', function () {
        if ($(".errorlog").html().trim() == "") {

            var r = confirm("Import Role . Are You sure ?");

            if (r) {
                $('#upload').val('Processing...')
                $('#upload').attr('disabled', 'disabled');

                var start = $("#uploadFrom").val();
                var end = $("#uploadTo").val();
                var template = $('#templates').val()
                var selectedenv = $('#env').val();

                var formData = new FormData();
                formData.append("start", start);
                formData.append("end", end);
                formData.append("env", selectedenv);
                formData.append("template", template);

                $.ajax({
                    url: CONTEXTPATH + "/api/insertValue",
                    type: "POST",
                    data: formData,
                    processData: false,  // tell jQuery not to process the data
                    contentType: false,  // tell jQuery not to set contentType
                    success: function (data) {
                        alert(data)
                        $('#upload').val('Upload')
                        $('#upload').removeAttr('disabled');
                    }
                })
            }
        } else {
            alert("There is an Error...")
        }
    })
});