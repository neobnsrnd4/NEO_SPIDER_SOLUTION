//전역 상태 플래그
let isRequestInProgress = false;

document.addEventListener("DOMContentLoaded", function () {

    // Bootstrap 툴팁 활성화
    let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    /* 체크 박스 이벤트 부여 */
    const selectAllCheckbox = document.getElementById("select-all");	//전체
    const rowCheckboxes = document.querySelectorAll(".row-checkbox");	//개별

    //1. 전체 선택/해제
    if(selectAllCheckbox) { // Cannot read properties of null (reading 'addEventListener') 오류 해결
        selectAllCheckbox.addEventListener("change", function () {
            const isChecked = selectAllCheckbox.checked;
            rowCheckboxes.forEach(checkbox => {
                checkbox.checked = isChecked;
            });
        });
    }
    //2. 개별 상태 변경 시, 전체 체크 박스 상태 변경
    rowCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", function () {
            const allChecked = Array.from(rowCheckboxes).every(cb => cb.checked);
            const noneChecked = Array.from(rowCheckboxes).every(cb => !cb.checked);
            if (allChecked) {
                selectAllCheckbox.checked = true;
                selectAllCheckbox.indeterminate = false;
            } else if (noneChecked) {
                selectAllCheckbox.checked = false;
                selectAllCheckbox.indeterminate = false;
            } else {
                selectAllCheckbox.indeterminate = true;
            }
        });
    });
    /* end of 체크 박스 이벤트 부여 */

    //모드 단일 토글 이벤트 부여
    const modeButtons = document.querySelectorAll(".btn-toggle-mode");
    modeButtons.forEach(button => {
        button.addEventListener("click", function () {
            toggleMode(button);
        });
    });

    //모드 전체 전환 이벤트 부여
    const mockButton = document.querySelector(".btn-api-mock");
    const realButton = document.querySelector(".btn-api-real");
    mockButton.addEventListener("click", function () {
        changeApiMode(false);
    });
    realButton.addEventListener("click", function () {
        changeApiMode(true);
    });

    //헬스 체크 이벤트 부여
    const healthButtons = document.querySelectorAll(".btn-healthcheck");
    healthButtons.forEach(button => {
        button.addEventListener("click", function () {
            performHealthCheck(button);
        });
    })

    //실행 이벤트 부여
    const executeButtons = document.querySelectorAll(".btn-execute");
    /*
	executeButtons.forEach(button => {
        button.addEventListener("click", function () {
            executeApi(button);
        });
    })
	*/
	executeButtons.forEach(button => {
        // API URL 가져오기
        const apiUrlElement = button.closest("tr").querySelector(".truncate-url");
        if (!apiUrlElement) return; // 요소가 없으면 무시

        const apiUrl = apiUrlElement.textContent.trim();

        // URL이 정규식 패턴을 포함하면 버튼 비활성화
		if (checkIfRegexUrl(apiUrl)) {
			button.disabled = true; // 버튼 비활성화
            button.classList.add("disabled"); // 스타일 적용
        } else {
            // 정규식이 없는 경우 정상 실행
            button.addEventListener("click", function () {
                executeApi(button);
            });
        }
    });

    // 추가 버튼 클릭 시 팝업 열기
    document.querySelector(".btn-api-add").addEventListener("click", function () {
        // 모달 요소에 data-type 속성 추가
        const modalElement = document.getElementById("apiModal");
        modalElement.setAttribute("data-type", "add");
        document.getElementById("apiModalLabel").textContent = "API 추가";

        // 모달 창 value reset
        $("#add-api-form")[0].reset();
        toggleRequestBody("httpMethod", "requestBodyContainer");

        const apiModal = new bootstrap.Modal(modalElement);
        apiModal.show();
    });

    // 삭제 버튼 이벤트
    const deleteButtons = document.querySelectorAll(".btn-delete");
    deleteButtons.forEach(button => {
        button.addEventListener("click", function () {
            deleteApi(button);
        });
    });

    // 수정, 복사 버튼 이벤트
    document.querySelectorAll(".btn-edit").forEach(button => {
        button.addEventListener("click", () => loadApiDetail(button));
    });

    // HTTP 메서드 변경 시 이벤트 적용
    document.getElementById("httpMethod").addEventListener("change", () => {
        toggleRequestBody("httpMethod", "requestBodyContainer");
    });
	
	// 체크박스 요소 처리
	const apiUrlInput = document.getElementById("apiUrl");
	const urlPatternCheckboxes = document.querySelectorAll('input[name="urlPattern"]');

	// 정규식 패턴 목록
	const regexPatterns = [
        /\/?\[0-9\]+\/*/,
        /\/?\[a-zA-Z\]+\/*/,
        /\/?\[a-zA-Z0-9\]+\/*/,
        /\/?\[a-zA-Z0-9-\]+\/*/
    ];

	// 체크박스 변경 이벤트 추가
    urlPatternCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", function () {
            let baseUrl = apiUrlInput.value.trim();

            // 기존 URL에서 정규식 패턴 제거 (불필요한 + 제거)
            regexPatterns.forEach(pattern => {
                baseUrl = baseUrl.replace(pattern, "").replace(/\+/, "").replace(/\/$/, ""); // 마지막 / 제거
            });

            // 다른 체크박스가 선택되었을 경우, 하나만 유지하도록 처리
            urlPatternCheckboxes.forEach(cb => {
                if (cb !== this) {
                    cb.checked = false;
                }
            });

            // 체크박스 해제 시 정규식 삭제 후 원래 URL 유지
            if (!this.checked) {
                apiUrlInput.value = baseUrl;
                return;
            }

            // 체크된 경우 새로운 정규식 추가
            let pattern = "";
            if (this.value === "digits") {
                pattern = "[0-9]+";
            } else if (this.value === "letters") {
                pattern = "[a-zA-Z]+";
            } else if (this.value === "alphanumeric") {
                pattern = "[a-zA-Z0-9]+";
            } else if (this.value === "special") {
                pattern = "[a-zA-Z0-9-]+";
            } else if (this.value === "any") {
                pattern = ".*";
            }

            // URL이 '/'로 끝나면 중복 방지 후 추가
            const endsWithSlash = baseUrl.endsWith("/");
            apiUrlInput.value = endsWithSlash ? baseUrl + pattern : baseUrl + "/" + pattern;
        });
    });
   

});

const toggleMode = (button) => {
    const id = button.getAttribute("data-id");

    fetch('/api/toggle-mode', {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: id
    })
        .then(response => {
            if (response.status === 200) {
                window.location.reload();
            } else {
                alert("시스템 오류가 발생하였습니다.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("네트워크 오류가 발생했습니다.");
        });
}

const changeApiMode = (targetMode) => {
    const checkboxes = document.querySelectorAll(".row-checkbox");
    const ids = getSelectedIdsFromCheckbox(checkboxes);

    if (ids.length === 0) {
        alert("전환할 API를 선택해주세요.");
        return;
    }

    fetch('/api/change-mode-selected', {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            ids: ids,
            targetMode
        })
    })
        .then(response => {
            if (response.status === 200) {
                if (targetMode === false) alert("선택한 API의 모드를 대응답으로 전환했습니다.");
                else alert("선택한 API 모드를 실서버로 전환했습니다.");
                window.location.reload();
            } else {
                alert("시스템 오류가 발생하였습니다.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("네트워크 오류가 발생했습니다.");
        });
}

const performHealthCheck = (button) => {
    if (isRequestInProgress) {
        alert("Health Check가 진행 중입니다. 잠시 후 다시 시도해주세요.");
        return;
    }

    const id = button.getAttribute("data-id");
    isRequestInProgress = true;//요청 상태 플래그 설정

    fetch('/api/health-check', {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: id
    })
        .then(response => {
            if (response.status === 200) {
                alert(`${id}번 API의 Health Check가 성공적으로 완료되었습니다.`);
                window.location.reload();
            } else {
                alert(`${id}번 API의 Health Check 진행 중 오류가 발생했습니다.`);
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("네트워크 오류가 발생했습니다.");
        })
        .finally(() => {
            isRequestInProgress = false;//요청 상태 플래그 해제
        })
}

const executeApi = (button) => {
    const id = button.getAttribute("data-id");
    const method = button.getAttribute("data-method");
    const newWindow = window.open("", "_blank");

    fetch(`/api/execute/${id}`, {
        method: method
    })
        .then(response => {
            // 리다이렉트 여부 확인
            if (response.redirected) {
                newWindow.location.href = response.url; // 실서버 URL로 이동
            } else {
                return response.json(); // Stub 데이터 처리
            }
        })
        .then(data => {
            if (data) {
                // Stub 데이터 표시
                newWindow.document.open();
                newWindow.document.write(`<pre>${JSON.stringify(data, null, 2)}</pre>`);
                newWindow.document.close();
            }
        })
        .catch(error => {
            console.error("Error:", error);
            newWindow.close();
            alert("API 실행 중 오류가 발생했습니다. 다시 시도해주세요.");
        });
}

const getSelectedIdsFromCheckbox = (checkboxes) => {
    const selectedIds = [];
    checkboxes.forEach(checkbox => {
        if (checkbox.checked) {
            const id = checkbox.getAttribute("data-id");
            selectedIds.push(parseInt(id, 10));
        }
    });
    return selectedIds;
}

const toggleRequestBody = (methodId, containerId) => {
    const method = document.getElementById(methodId).value;
    const requestBodyContainer = document.getElementById(containerId);

    if (method === "GET" || method === "DELETE") {
        requestBodyContainer.style.display = "none"; // GET, DELETE는 Request Body 숨김
    } else {
        requestBodyContainer.style.display = "block"; // POST, PUT은 Request Body 보임
    }
};

document.getElementById("saveApiButton").addEventListener("click", function () {
    const dataType = document.getElementById("apiModal").getAttribute("data-type");
    console.log(dataType);
    if(dataType === "add" || dataType === "copy"){
        addApi();
    } else if(dataType === "edit"){
        updateApi();
    }
});

// API 등록 함수
const addApi = () => {
    // 1. 입력값 가져오기
    const apiName = document.getElementById("apiName").value.trim();
    const apiUrl = document.getElementById("apiUrl").value.trim();
    const httpMethod = document.getElementById("httpMethod").value;
    //const requestHeaders = document.getElementById("requestHeaders").value.trim();
    const requestBody = document.getElementById("requestBody").value.trim();
    const responseStatusCode = parseInt(document.getElementById("responseStatusCode").value);
    const responseBody = document.getElementById("responseBody").value.trim();

    // 2. 필수 입력값 검증
    if (!apiName || !apiUrl) {
        alert("API 이름과 URL을 입력해주세요.");
        return;
    }

    // JSON 유효성 검사
    if (!validateJsonFields(httpMethod, "requestBody", "responseBody")) {
        return; // JSON 오류 발생 시 저장하지 않음
    }

    // 3. 요청 데이터 객체 생성
    const requestData = {
        mockApiName: apiName,
        mockApiUrl: apiUrl,
        mockApiRequestMethod: httpMethod,
        //requestHeaders: requestHeaders || null,
        responseStatusCode: responseStatusCode,
        responseBody: responseBody || null
    };

    // 4. GET, DELETE 요청일 경우 requestBody 제거
    if (httpMethod !== "GET" && httpMethod !== "DELETE") {
        requestData.requestBody = requestBody || null;
    }

    // 5. 백엔드로 요청 보내기 (POST /api/add)
    fetch("/api/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("API 등록에 실패했습니다.");
            }
            return response.json();
        })
        .then(data => {
            alert("API 등록 완료!");
            location.reload(); // 페이지 새로고침하여 업데이트된 목록 반영
        })
        .catch(error => {
            console.error("Error:", error);
            alert("API 등록 중 오류 발생!");
        });
}

// API 삭제 함수 
const deleteApi = (button) => {
    const apiId = button.getAttribute("data-id");

    if (!apiId) {
        alert("API ID를 찾을 수 없습니다.");
        return;
    }

    if (confirm("정말 삭제하시겠습니까?")) {
        fetch(`/api/delete/${apiId}`, {
            method: "DELETE",
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("삭제 실패");
                }
                return response.text();
            })
            .then(() => {
                alert("API 삭제 완료!");
                location.reload(); // 페이지 새로고침하여 목록 반영
            })
            .catch(error => {
                console.error("삭제 오류:", error);
                alert("API 삭제 중 오류 발생!");
            });
    }
};

// API 상세 정보 불러오기
const loadApiDetail = (button) => {
    const apiId = button.getAttribute("data-id");
    const dataType = button.getAttribute("data-type");
    console.log(apiId, dataType);

    if (!apiId) {
        alert("API ID를 찾을 수 없습니다.");
        return;
    }

    fetch(`/api/get/${apiId}`)
        .then(response => response.json())

        .then(data => {
            if (!data.api || !data.wiremock) {
                alert("API 정보를 불러올 수 없습니다.");
                return;
            }
            document.getElementById("apiId").value = apiId;
            // 1. DB에서 가져온 데이터 (메타데이터)
            document.getElementById("apiName").value = dataType === "copy" ? data.api.mockApiName + "_copy" : data.api.mockApiName;
            document.getElementById("apiUrl").value = data.api.mockApiUrl;
			
			// 체크박스 상태 초기화
            const urlPatternCheckboxes = document.querySelectorAll('input[name="urlPattern"]');
            urlPatternCheckboxes.forEach(cb => cb.checked = false); // 모든 체크박스 해제

            // URL 값으로 체크박스 상태 설정
            const url = data.api.mockApiUrl;

            if (url.includes("[0-9]+")) {
                document.getElementById("enableDigits").checked = true;
            } else if (url.includes("[a-zA-Z]+")) {
                document.getElementById("enableLetters").checked = true;
            } else if (url.includes("[a-zA-Z0-9]+")) {
                document.getElementById("enableAlphanumeric").checked = true;
            } else if (url.includes("[a-zA-Z0-9-]+")) {
                document.getElementById("enableSpecialChars").checked = true;
            } else if (url.includes(".*")) {
                document.getElementById("enableAny").checked = true;
            }

            // 2. WireMock에서 가져온 Request/Response 데이터
            document.getElementById("httpMethod").value = data.wiremock.body.request.method;
            //document.getElementById("editRequestHeaders").value = JSON.stringify(data.wiremock.body.request.headers || {}, null, 2);
            // Request Body - equalToJson 제거
            let requestBody = "";
            if (data.wiremock.body.request.bodyPatterns && data.wiremock.body.request.bodyPatterns.length > 0) {
                const bodyPattern = data.wiremock.body.request.bodyPatterns[0]; // 첫 번째 패턴만 사용
                if (bodyPattern.equalToJson) {
                    try {
                        requestBody = JSON.stringify(JSON.parse(bodyPattern.equalToJson), null, 2); // JSON 변환
                    } catch (e) {
                        requestBody = bodyPattern.equalToJson; // 변환 실패 시 원본 그대로 유지
                    }
                }
            }
            document.getElementById("requestBody").value = requestBody;
            document.getElementById("responseStatusCode").value = data.wiremock.body.response.status;

            // JSON을 파싱하여 \가 붙지 않도록 처리
            try {
                document.getElementById("responseBody").value = JSON.stringify(JSON.parse(data.wiremock.body.response.body), null, 2);
            } catch (e) {
                document.getElementById("responseBody").value = data.wiremock.body.response.body; // JSON이 아니면 그대로 출력
            }

            toggleRequestBody("httpMethod", "requestBodyContainer");

            // 3. 수정 모달창 띄우기
            // 모달 요소에 data-type 속성 추가
            const modalElement = document.getElementById("apiModal");
            modalElement.setAttribute("data-type", dataType);
            if(dataType === "edit"){
                document.getElementById("apiModalLabel").textContent = "API 수정";
            }else if(dataType === "copy"){
                document.getElementById("apiModalLabel").textContent = "API 복사";
            }

            const apiModal = new bootstrap.Modal(modalElement);
            apiModal.show();
        })
        .catch(error => {
            console.error("API 상세 정보 불러오기 실패:", error);
            alert("API 정보를 불러오는 중 오류가 발생했습니다.");
        });
};

// API 수정 요청 함수
const updateApi = () => {
    const apiId = document.getElementById("apiId").value;
    if (!apiId) {
        alert("API ID를 찾을 수 없습니다.");
        return;
    }
    const httpMethod = document.getElementById("httpMethod").value;
    // JSON 유효성 검사 (요청 본문, 응답 본문)
    if (!validateJsonFields(httpMethod, "requestBody", "responseBody")) {
        return; // JSON 오류 발생 시 저장하지 않음
    }

    // 응답 본문을 JSON 문자열로 변환하여 저장
    let responseBody = document.getElementById("responseBody").value.trim();
    try {
        responseBody = JSON.stringify(JSON.parse(responseBody)); // JSON 형식 유지
    } catch (e) {
        // JSON이 아닐 경우 변환하지 않고 그대로 저장
    }
    // 1. 수정된 데이터 가져오기
    const requestData = {
        mockApiName: document.getElementById("apiName").value.trim(),
        mockApiUrl: document.getElementById("apiUrl").value.trim(),
        mockApiRequestMethod: document.getElementById("httpMethod").value,
        //requestHeaders: document.getElementById("editRequestHeaders").value.trim(),
        requestBody: document.getElementById("requestBody").value.trim(),
        responseStatusCode: parseInt(document.getElementById("responseStatusCode").value, 10),
        responseBody: responseBody
    };

    // 2. GET, DELETE 요청일 경우 requestBody 제외
    if (requestData.httpMethod === "GET" || requestData.httpMethod === "DELETE") {
        delete requestData.requestBody;
    }

    // 3. 백엔드로 수정 요청 보내기
    fetch(`/api/update/${apiId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("API 수정 실패");
            }
            return response.text();
        })
        .then(() => {
            alert("API 수정 완료!");
            location.reload(); // 페이지 새로고침하여 변경된 목록 반영
        })
        .catch(error => {
            console.error("API 수정 오류:", error);
            alert("API 수정 중 오류 발생!");
        });
};

const validateJsonFields = (httpMethod, requestBodyId, responseBodyId) => {
    let invalidFields = [];

    // 1. 요청 본문(JSON) 검사 (POST, PUT일 때만)
    if (httpMethod === "POST" || httpMethod === "PUT") {
        const requestBody = document.getElementById(requestBodyId).value.trim();
        if (requestBody && !isValidJson(requestBody)) {
            invalidFields.push("요청 본문");
        }
    }

    // 2. 응답 본문(JSON) 검사
    const responseBody = document.getElementById(responseBodyId).value.trim();
    if (responseBody && !isValidJson(responseBody)) {
        invalidFields.push("응답 본문");
    }

    // 3. 오류 메시지 출력 (유효하지 않은 필드가 있을 경우)
    if (invalidFields.length > 0) {
        alert(`${invalidFields.join(", ")} 필드의 JSON 형식이 올바르지 않습니다.`);
        return false;
    }

    return true;
};

// **JSON 유효성 검사 함수**
const isValidJson = (jsonString) => {
    try {
        JSON.parse(jsonString);
        return true;
    } catch (e) {
        return false;
    }
};

// 정규식 포함 여부 확인 함수
function checkIfRegexUrl(url) {
	const regexPatterns = [
	        /\[0-9\]+/,        // 숫자 포함
	        /\[a-zA-Z\]+/,     // 문자 포함
	        /\[a-zA-Z0-9\]+/,  // 문자 + 숫자 포함
	        /\[a-zA-Z0-9-\]+/ // 문자 + 숫자 + 하이픈 포함     
	    ];
    return regexPatterns.some(pattern => pattern.test(url)); // 정규식 포함 여부 반환
}