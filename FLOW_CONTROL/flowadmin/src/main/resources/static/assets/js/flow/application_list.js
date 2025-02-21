// 어플리케이션 등록 모달창 열기
document.getElementById('openAppCreateModal').onclick = () => {
    const modalElement = document.getElementById('appCreateModal');
    const applicationModal = new bootstrap.Modal(modalElement);
    applicationModal.show();
}

// 어플리케이션 삭제
document.querySelectorAll('.delete-app').forEach(button => {
    button.addEventListener("click", function(){
        const dataValue = this.getAttribute("data-value");
        if (confirm("삭제하시겠습니까?")) {
            const searchState = JSON.parse(sessionStorage.getItem('searchState'));
            const {paramPath, page, ...params} = searchState;
            location.href = "/admin/flow/delete?applicationId=" + dataValue + "&" + new URLSearchParams(params).toString();
        }
    });
});