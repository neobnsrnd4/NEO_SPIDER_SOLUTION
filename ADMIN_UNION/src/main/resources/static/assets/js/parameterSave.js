/*
 * 이 자바스크립트 파일은 검색 페이지(중요)의 상단에 삽입하여야 한다.
 */

function getSearchState(){
    return JSON.parse(sessionStorage.getItem("searchState"));
}

function getCurrentPage(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    let currentPage = urlParams.get('page');

    //기본 페이지는 1
    if (currentPage == null){
        currentPage = '1';
    }
    return currentPage;
}

function initSearchState(){
    const pathName = window.location.pathname;
    const currentPage = getCurrentPage();
    const value = {"paramPath": pathName, "page":currentPage};
    sessionStorage.setItem("searchState", JSON.stringify(value));
}

//DOM이 준비되었을 때 자동으로 실행
document.addEventListener('DOMContentLoaded', function(){
    const searchState = getSearchState();
    const queryString = window.location.search;

    //searchState 가 스토리지에 저장되어 있지 않을 때
    //searchState 파라미터가 이 페이지에서 쓰이는 파라미터가 아닐 때
    //이 페이지에서 쓰이는 파라미터는 맞지만 현재 겟 쿼리 없이 해당 페이지에 재접속했을 때
    //searchState 초기화를 수행하고 함수를 종료한다.
    if (searchState == null || searchState.paramPath !== window.location.pathname || queryString === ""){
        initSearchState();
        return null;
    }

    //겟 쿼리를 분석하여 파라미터 정보 최신화
    const urlParams = new URLSearchParams(queryString);
    urlParams.forEach(
        (value, key) => {
            if (key === 'page'){
                // searchState = getCurrentPage();
                searchState.page = getCurrentPage();
            } else {
                searchState[key] = value;
            }
        }
    );
    if (!urlParams.has('page')){
        searchState.page = '1';
    }
    if (!urlParams.has('size')){
        searchState.size = '10';
    }

    //최종 반영
    sessionStorage.setItem("searchState", JSON.stringify(searchState));

})