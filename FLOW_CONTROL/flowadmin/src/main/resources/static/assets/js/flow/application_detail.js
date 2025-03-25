// 어플리케이션 수정 모달창 열기
document.getElementById('openAppUpdateModal').onclick = () => {
	const modalElement = document.getElementById('appUpdateModal');
	const applicationModal = new bootstrap.Modal(modalElement);
	applicationModal.show();
}

/***** 벌크헤드 관련 자바스크립트 *****/
// 벌크헤드 모달창 열기
document.querySelectorAll(".open-bulkhead-modal").forEach(button => {
	button.addEventListener("click", function() {
		// 추가 | 수정 여부 전달
		const dataType = this.getAttribute("data-type");
		$('#saveBulkheadBtn').attr("data-type", dataType);

		if (dataType === 'update') { // 수정 모달창의 경우 value 값 세팅
			$('#bulkheadModalLabel').text("동시 수행 제한 수정");
			const dataId = this.getAttribute('data-id');
			$.ajax({
				type: 'GET',
				url: '/admin/flow/bulkhead/find?bulkheadId=' + dataId,
				success: function(response) {
					$('#bulkheadId').val(response.bulkheadId);
					$('#bulkheadUrl').val(response.url).prop('disabled', true);
					$('#maxConcurrentCalls').val(parseInt(response.maxConcurrentCalls));
					$("#maxWaitDuration").val(parseInt(response.maxWaitDuration));
				}
			});
		} else if (dataType === 'create') { // 추가 모달창의 경우 value 값 리셋
			$('#bulkheadUrl').prop('disabled', false);
			$('#bulkheadModalLabel').text("동시 수행 제한 등록");
			$('#bulkheadModalForm')[0].reset();
		}

		const modalElement = document.getElementById('bulkheadModal');
		const bulkheadModal = new bootstrap.Modal(modalElement);
		bulkheadModal.show();
	});
});

// 벌크헤드 모달창 버튼 이벤트
$('#saveBulkheadBtn').on('click', function() {
	const dataType = $(this).attr('data-type');
	if (dataType === 'create') {
		createBulkhead();
	} else if (dataType === 'update') {
		updateBulkhead();
	}
})

// 벌크헤드 추가
const createBulkhead = () => {
	const data = {
		'applicationId': $('#bulkheadAppId').val(),
		'url': $('#bulkheadUrl').val(),
		'maxConcurrentCalls': $('#maxConcurrentCalls').val(),
		'maxWaitDuration': $('#maxWaitDuration').val(),
	};
	$.ajax({
		type: 'POST',
		url: '/admin/flow/bulkhead/create',
		data: JSON.stringify(data),
		contentType: 'application/json',
		success: function(response) {
			location.reload();
		},
		error: function(xhr) {
			alert(xhr.responseText);
		}
	});
};

// 벌크헤드 수정
const updateBulkhead = () => {
	const data = {
		'bulkheadId': $('#bulkheadId').val(),
		'applicationId': $('#bulkheadAppId').val(),
		'url': $('#bulkheadUrl').val(),
		'maxConcurrentCalls': $('#maxConcurrentCalls').val(),
		'maxWaitDuration': $('#maxWaitDuration').val(),
	};
	$.ajax({
		type: 'POST',
		url: '/admin/flow/bulkhead/update',
		data: JSON.stringify(data),
		contentType: 'application/json',
		success: function(response) {
			location.reload();
		},
		error: function(xhr) {
			console.log(xhr.responseText);
		}
	});
};

// 벌크헤드 삭제
$('.delete-bulkhead').each(function(index, button) {
	$(button).click(function() {
		if (confirm("삭제하시겠습니까?")) {
			const dataId = $(button).attr('data-id');
			const dataName = $(button).attr('data-name');
			$.ajax({
				type: 'GET',
				url: '/admin/flow/bulkhead/delete?bulkheadId=' + dataId + '&applicationName=' + dataName,
				success: function(response) {
					alert(response);
					location.reload();
				},
				error: function(xhr) {
					alert(xhr.responseText);
				}
			})
		}
	});
});

/***** 레이트리미터 관련 자바스크립트 *****/
// 레이트리미터 모달창 열기
document.querySelectorAll(".open-rateLimiter-modal").forEach(button => {
	button.addEventListener("click", function() {
		// 추가 | 수정 여부 전달
		const dataType = this.getAttribute("data-type");
		$('#saveRateLimiter').attr("data-type", dataType);

		if (dataType === 'update') { // 수정 모달창의 경우 value 값 세팅
			const dataId = this.getAttribute('data-id');
			$.ajax({
				type: 'GET',
				url: '/admin/flow/ratelimiter/find?ratelimiterId=' + dataId,
				success: function(response) {
					$('#bulkheadModalLabel').text("요청 제한 수정");
					$('#rateLimiterId').val(response.ratelimiterId);
					$('#rateLimiterType').val(response.type);
					$('#rateLimiterType').prop('disabled', true);
					$('#rateLimiterUrl').val(response.url);
					$('#limitForPeriod').val(parseInt(response.limitForPeriod));
					$('#limitRefreshPeriod').val(parseInt(response.limitRefreshPeriod));
					$('#timeoutDuration').val(parseInt(response.timeoutDuration));


					$('#rateLimiterUrl').prop('disabled', true);
					// if (response.type !== 1){
					//     $('#rateLimiterUrl').prop('disabled', true);
					// } else {
					//     $('#rateLimiterUrl').prop('disabled', false);
					// }
				}
			});
		} else if (dataType === 'create') { // 추가 모달창의 경우 value 값 리셋
			$('#bulkheadModalLabel').text("요청 제한 등록");
			$('#rateLimiterModalForm')[0].reset();
			$('#rateLimiterType').prop('disabled', false);
			$('#rateLimiterUrl').prop('disabled', false);
		}

		const modalElement = document.getElementById('rateLimiterModal');
		const rateLimiterModal = new bootstrap.Modal(modalElement);
		rateLimiterModal.show();
	});
});

// 레이트리미터 모달창 버튼 이벤트
$('#saveRateLimiter').on('click', function() {
	const dataType = $(this).attr('data-type');
	if (dataType === 'create') {
		createRateLimiter();
	} else if (dataType === 'update') {
		updateRateLimiter();
	}
})

// 레이트리미터 추가
const createRateLimiter = () => {
	const data = {
		'applicationId': $('#rateLimiterAppId').val(),
		'url': $('#rateLimiterUrl').val(),
		'type': $('#rateLimiterType').val(),
		'limitForPeriod': $('#limitForPeriod').val(),
		'limitRefreshPeriod': $('#limitRefreshPeriod').val(),
		'timeoutDuration': $('#timeoutDuration').val(),
	};
	if (data.type === 1) {
		data["url"] = $('#rateLimiterUrl').val();
	}
	$.ajax({
		type: 'POST',
		url: '/admin/flow/ratelimiter/create',
		data: JSON.stringify(data),
		contentType: 'application/json',
		success: function(response) {
			location.reload();
		},
		error: function(xhr) {
			alert(xhr.responseText);
		}
	});
};

// 레이트리미터 추가 시 패턴에 따른 input 창 유무 변경
$('#rateLimiterType').on('change', function() {
	let inputElement = `<label class="form-label" for="rateLimiterUrl">URL 패턴</label>
                                <input class="form-control" type="text" id="rateLimiterUrl" name="url" placeholder="예) /product/*" required/>`;
	if ($(this).val() === '1') {
		$("#rateLimiterUrlDiv").append(inputElement);
	} else {
		$("#rateLimiterUrlDiv").empty();
	}
});

// 레이트리미터 수정
const updateRateLimiter = () => {
	const data = {
		'ratelimiterId': $('#rateLimiterId').val(),
		'applicationId': $('#rateLimiterAppId').val(),
		'url': $('#rateLimiterUrl').val(),
		'type': $('#rateLimiterType').val(),
		'limitForPeriod': $('#limitForPeriod').val(),
		'limitRefreshPeriod': $('#limitRefreshPeriod').val(),
		'timeoutDuration': $('#timeoutDuration').val(),
	};
	$.ajax({
		type: 'POST',
		url: '/admin/flow/ratelimiter/update',
		data: JSON.stringify(data),
		contentType: 'application/json',
		success: function(response) {
			location.reload();
		},
		error: function(xhr) {
			console.log(xhr.responseText);
		}
	});
};

// 레이트리미터 삭제
$('.delete-ratelimiter').each(function(index, button) {
	$(button).click(function() {
		if (confirm("삭제하시겠습니까?")) {
			const dataId = $(button).attr('data-id');
			const dataName = $(button).attr('data-name');
			$.ajax({
				type: 'GET',
				url: '/admin/flow/ratelimiter/delete?ratelimiterId=' + dataId + '&applicationName=' + dataName,
				success: function(response) {
					alert(response);
					location.reload();
				},
				error: function(xhr) {
					alert(xhr.responseText);
				}
			})
		}
	});
});




/***** 레디스 레이트리미터 관련 자바스크립트 *****/
// 레디스 레이트리미터 모달창 열기
document.querySelectorAll(".open-rateLimiter-modal-by-redis").forEach(button => {
	button.addEventListener("click", function() {
		// 추가 | 수정 여부 전달
		const dataType = this.getAttribute("data-type");
		$('#saveRateLimiterByRedis').attr("data-type", dataType);

		if (dataType === 'update') { // 수정 모달창의 경우 value 값 세팅
			const dataId = this.getAttribute('data-id');
			$.ajax({
				type: 'GET',
				url: '/admin/flow/ratelimiterByRedis/find?ratelimiterId=' + dataId,
				success: function(response) {
					$('#rateLimiterModalLabelByRedis').text("요청 제한 수정");
					$('#rateLimiterIdByRedis').val(response.ratelimiterId);
					$('#rateLimiterTypeByRedis').val(response.type);
					$('#rateLimiterTypeByRedis').prop('disabled', true);
					$('#rateLimiterUrlByRedis').val(response.url);
					$('#limitForPeriodByRedis').val(parseInt(response.limitForPeriod));
					$('#limitRefreshPeriodByRedis').val(parseInt(response.limitRefreshPeriod));
					$('#timeoutDurationByRedis').val(parseInt(response.timeoutDuration));


					$('#rateLimiterUrlByRedis').prop('disabled', true);
					// if (response.type !== 1){
					//     $('#rateLimiterUrl').prop('disabled', true);
					// } else {
					//     $('#rateLimiterUrl').prop('disabled', false);
					// }
				}
			});
		} else if (dataType === 'create') { // 추가 모달창의 경우 value 값 리셋
			$('#rateLimiterModalLabelByRedis').text("요청 제한 등록");
			$('#rateLimiterModalFormByRedis')[0].reset();
			$('#rateLimiterTypeByRedis').prop('disabled', false);
			$('#rateLimiterUrlByRedis').prop('disabled', false);
		}

		const modalElement = document.getElementById('rateLimiterModalByRedis');
		const rateLimiterModal = new bootstrap.Modal(modalElement);
		rateLimiterModal.show();
	});
});

// 레이트리미터 모달창 버튼 이벤트
$('#saveRateLimiterByRedis').on('click', function() {
	const dataType = $(this).attr('data-type');
	if (dataType === 'create') {
		createRateLimiterByRedis();
	} else if (dataType === 'update') {
		updateRateLimiterByRedis();
	}
})

// 레이트리미터 추가
const createRateLimiterByRedis = () => {
	const data = {
		'applicationId': $('#rateLimiterAppIdByRedis').val(),
		'url': $('#rateLimiterUrlByRedis').val(),
		'type': $('#rateLimiterTypeByRedis').val(),
		'limitForPeriod': $('#limitForPeriodByRedis').val(),
		'limitRefreshPeriod': $('#limitRefreshPeriodByRedis').val(),
		'timeoutDuration': $('#timeoutDurationByRedis').val(),
	};
	if (data.type === 1) {
		data["url"] = $('#rateLimiterUrlByRedis').val();
	}
	$.ajax({
		type: 'POST',
		url: '/admin/flow/ratelimiterByRedis/create',
		data: JSON.stringify(data),
		contentType: 'application/json',
		success: function(response) {
			location.reload();
		},
		error: function(xhr) {
			alert(xhr.responseText);
		}
	});
};

// 레이트리미터 추가 시 패턴에 따른 input 창 유무 변경
$('#rateLimiterTypeByRedis').on('change', function() {
	let inputElement = `<label class="form-label" for="rateLimiterUrlByRedis">URL 패턴</label>
                                <input class="form-control" type="text" id="rateLimiterUrlByRedis" name="url" placeholder="예) /product/*" required/>`;
	if ($(this).val() === '1') {
		$("#rateLimiterUrlDivByRedis").append(inputElement);
	} else {
		$("#rateLimiterUrlDivByRedis").empty();
	}
});

// 레디스 레이트리미터 수정
const updateRateLimiterByRedis = () => {
	const data = {
		'ratelimiterId': $('#rateLimiterIdByRedis').val(),
		'applicationId': $('#rateLimiterAppIdByRedis').val(),
		'url': $('#rateLimiterUrlByRedis').val(),
		'type': $('#rateLimiterTypeByRedis').val(),
		'limitForPeriod': $('#limitForPeriodByRedis').val(),
		'limitRefreshPeriod': $('#limitRefreshPeriodByRedis').val(),
		'timeoutDuration': $('#timeoutDurationByRedis').val(),
	};
	$.ajax({
		type: 'POST',
		url: '/admin/flow/ratelimiterByRedis/update',
		data: JSON.stringify(data),
		contentType: 'application/json',
		success: function(response) {
			location.reload();
		},
		error: function(xhr) {
			console.log(xhr.responseText);
		}
	});
};

// 레디스 레이트리미터 삭제
$('.delete-ratelimiter-by-redis').each(function(index, button) {
	$(button).click(function() {
		if (confirm("삭제하시겠습니까?")) {
			const dataId = $(button).attr('data-id');
			const dataName = $(button).attr('data-name');
			$.ajax({
				type: 'GET',
				url: '/admin/flow/ratelimiterByRedis/delete?ratelimiterId=' + dataId + '&applicationName=' + dataName,
				success: function(response) {
					alert(response);
					location.reload();
				},
				error: function(xhr) {
					alert(xhr.responseText);
				}
			})
		}
	});
});


// ratelimiter 토글 버튼 클릭
$('.toggle-rateLimiter-card').click(function() {
	if (confirm("Ratelimiter을 전환하시겠습니까?")) {

		const data = {
			'applicationId': $('#rateLimiterAppId').val(),
			'applicationName': $(this).attr('data-name'),
			'ratelimiterMode': $(this).attr('data-type'),
		};

		const $currentCard = $(this).closest('.ratelimiter-card')
		$.ajax({
			type: 'POST',
			url: '/admin/flow/ratelimiterByRedis/toggle',
			data: JSON.stringify(data),
			contentType: 'application/json',
			success: function(response) {
				// 토글
				const $all = $('.ratelimiter-card')
				const $filterd = $all.not($currentCard[0])

				$currentCard.css('display', 'none')
				$filterd.css('display', 'flex')
				alert(response);
			},
			error: function(xhr) {
				alert(xhr.responseText);
			}
		})
	}
});









