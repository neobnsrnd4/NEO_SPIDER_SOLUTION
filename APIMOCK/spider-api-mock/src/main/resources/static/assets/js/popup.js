

document.querySelectorAll('.text-start.trace-id a').forEach(link => {
		    link.addEventListener('click', function (event) {
		        event.preventDefault(); // 기본 링크 이동 동작 방지

		        const url = this.href; // 클릭된 링크의 URL 가져오기
		        const popupWidth = Math.min(window.screen.width * 0.9, 1400); // 화면의 90% 또는 최대 1200px
		        const popupHeight = Math.min(window.screen.height * 0.9, 900); // 화면의 90% 또는 최대 900px
		        const left = (window.screen.width - popupWidth) / 2; // 화면 중앙 계산
		        const top = (window.screen.height - popupHeight) / 2; // 화면 중앙 계산

		        // 팝업 창 열기
		        window.open(
		            url,
		            '_blank',
		            `width=${popupWidth},height=${popupHeight},top=${top},left=${left},resizable=yes,scrollbars=yes`
		        );

		        console.log(`팝업 창 열림: ${url}`);
		    });
		});
