// innerHTML을 사용하여 요소를 생성하는 유틸리티 함수
function createElementWithHTML(tag, html) {
	const element = document.createElement(tag);
	element.innerHTML = html;
	return element;
}

// Java에서 테이블 정보를 가져와 목록으로 표시하는 함수
function listTables() {
	try {
		const tablesContainer = document.getElementById("tables");
		tablesContainer.innerHTML = `<h6 style="font-weight: bold">테이블 목록</h6>`;
		
		// DB 정보
		const url = document.getElementById("dbUrl").value;
		const username = document.getElementById("username").value;
		const password = document.getElementById("password").value;
		// 저장할 폴더명
		const targetPath = document.getElementById("targetPath").value;
		
		if (!url || !username || !password || !targetPath) {
			alert("URL, 사용자 이름, 비밀번호 및 파일명을 모두 입력해주세요.");
			return;
		}

		const tableList = JSON.parse(invokeListTables(url, username, password)); // Java의 ListTables 메소드 호출

		if (tableList && Array.isArray(tableList)) {
			tableList.forEach(table => {
		        // HTML 표 생성
		        const tableHTML = `
					<div class="row align-items-center">
						<div class="col-auto" style="font-size: 0.875rem; font-weight: bold">
							${table.tableName}</div>
						<button type="button" class="btn btn-outline-secondary btn-sm col-auto"
							onClick='generateCode("${url}", "${username}", "${password}", "${table.tableName}", "${targetPath}")'>
							생성</button></div>
		            <table class="table w-auto" style="font-size: 0.875rem">
		                <thead>
		                    <tr>
		                        <th>컬럼명</th>
		                        <th>데이터타입</th>
		                        <th>기본키</th>
		                    </tr>
		                </thead>
		                <tbody>
		                    ${table.columns.map(column => `
		                        <tr>
		                            <td>${column.columnName}</td>
		                            <td class="text-center">${column.dataType}</td>
		                            <td class="text-center"><input type="radio" name="${table.tableName}_pk" value="${column.columnName}" ${column.isPrimaryKey ? "checked" : ""} style="pointer-events: none;"/></td>
		                        </tr>
		                    `).join('')}
		                </tbody>
		            </table>
		        `;
				
				const tableElement = createElementWithHTML('div', tableHTML);
				/*tableElement.classList.add("my-3");*/
				tablesContainer.appendChild(tableElement);
			});
		} else {
			alert("유효하지 않은 값입니다.");
		}

	} catch (e) {
		alert(`오류가 발생했습니다: ${e.message}`);
	}
}

// 기본키를 확인하고 generateCode 호출
function generateCode(url, username, password, tableName, targetPath) {
	invokeGenerateCode(url, username, password, targetPath, tableName);
}

