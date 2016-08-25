$('.input-group.date').datepicker({
                format: "dd.mm.yyyy",
                weekStart: 1,
                todayBtn: "linked",
                clearBtn: true,
                daysOfWeekHighlighted: "0,6",
                calendarWeeks: true,
                autoclose: true,
                todayHighlight: true,
                datesDisabled: ['08/06/2016', '08/21/2016']
            });