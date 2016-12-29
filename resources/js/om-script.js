function headerClick(cmp){
    var parent = $(cmp).parent(),
        superParent = parent.parent().children('i:nth-child(1)');

    parent.children('.list').toggle('boxed')

    if (superParent.attr('class').indexOf('open') == -1 ) {
        superParent.attr('class', 'folder open outline icon');
    }else{
        superParent.attr('class', 'folder outline icon');
    }
}


$('.menu .item').tab();
$('.ui.radio.checkbox').checkbox();



