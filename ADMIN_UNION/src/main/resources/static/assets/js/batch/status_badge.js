$('.exec-status.badge').each(function() {
    const execStatus = $(this).text();
    let statusClass = '';

    if(execStatus === 'COMPLETED'){
        statusClass = 'bg-success';
    } else if(execStatus === 'FAILED' || execStatus === 'UNKNOWN'){
        statusClass = 'bg-danger';
    } else {
        statusClass = 'bg-warning';
    }

    $(this).parent().addClass('m-0');
    $(this).addClass(statusClass);
})
