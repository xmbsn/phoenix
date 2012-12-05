var continuous_err_times = 0;
var MAX_CON_ERR_TIMES = 10;

$(function(){
	if (is_version_creating()) {
		fetch_create_log();
	}
	bind_cmp_evt_handlers();
});

function fetch_create_log() {
	$.ajax("", {
		data : $.param({
            "op" : "status",
            "version" : $("#creating_version").val(),
            "index" : $("#log_index").val()
        }, true),
        dataType: "json",
        cache: false,
        success: function(result) {
            continuous_err_times = 0;
            if (result != null) {
            	update_create_log(result);
            }
        },
        error: function(xhr, errstat, err) {
            continuous_err_times++;
        },
        complete: function() {
            if (is_version_creating() && continuous_err_times < MAX_CON_ERR_TIMES) {
                 setTimeout(fetch_create_log, 1000);
            }
        }
	});
}

function update_create_log(result) {
	var creating_version = $("#creating_version").val();
	var return_version = result.version;
	if (creating_version == return_version) {
		$("#log_index").val(result.index);
		var $logplane = $("#log-plane");
		$logplane.append("<div class=\"terminal-like\">" + result.log + "</div>");
		$logplane.scrollTop($logplane.get(0).scrollHeight);
	} else {
		$("#creating_version").val("");
	}
}

function is_version_creating() {
	return !$("#creating_version").val().isBlank();
}

var confirm_timeout_id;

function bind_cmp_evt_handlers() {
	$(".version_row").hover(
		function() {
			$(this).find(".btn_container").show();
		},
		function() {
			$(this).find(".btn_container").hide();
		}
	);
	
	$("[name='btn_del']").click(function() {
		clearTimeout(confirm_timeout_id);
		$("#del_version").val($(this).attr("version"));
		$("#del_confirm").fadeIn('slow', function() {
			$this = $(this);
			confirm_timeout_id = setTimeout(function() {
				$this.fadeOut('slow');
			}, 3000);
		});
	});
	
	$("#del_cancel").click(function() {
		$("#del_confirm").hide();
		return false;
	});
	
	$("#del_confirm").click(function() {
		var removeuri = "/console/version?op=remove&id=" + $("#del_version").val();
		$(location).attr("href", removeuri.prependcontext());
		return false;
	});
	
	$("#create_btn").click(function() {
		var version = $("#version").val();
		$("#error_msg").text("");
		if (version.isBlank()) {
			$("#error_msg").text("Version必填!");
			return false;
		}
	});
	
}