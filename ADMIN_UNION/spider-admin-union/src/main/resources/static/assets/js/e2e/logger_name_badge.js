$('.logger-name.badge').each(function() {
    const loggerName = $(this).text();
    let badgeClass = '';

    if(loggerName === 'TRACE'){
        badgeClass = 'badge-outline-dark';
    }else if(loggerName === 'DELAY'){
        badgeClass = 'badge-outline-warning';
    }else{
        badgeClass = 'badge-outline-danger';
    }

    $(this).parent().addClass('m-0')
    $(this).addClass(badgeClass);
})
