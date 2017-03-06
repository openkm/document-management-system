var TABLE = {};

TABLE.fixHeader = function(table) {
  $(table).each(function() {
    var $table = $(this);
    var $thead = $table.find('thead');
    var $ths = $thead.find('th');
    var timer = false;
    $table.data('top', $thead.offset().top);
    $table.data('left', $thead.offset().left);
    $table.data('bottom', $table.data('top') + $table.height() - $thead.height());
    var $list = $('<ul class="faux-head"></ul>');
    var idx = 0;
    
    $ths.each(function(i) {
      _th = $(this);
      
      if (/chrome/.test(navigator.userAgent.toLowerCase())) {
    	if (idx++ == 0) {
          $list.append($("<li style='border-left: 1px solid #A5A596;'></li>")
            .addClass(_th.attr("class")).html(_th.html()).width(_th.width() + 1)
            .click(function(){
              _th.click();
            })
          ).hide().css({left: $table.data('left'), top: $table.data('top')});
    	} else {
          $list.append($("<li></li>")
            .addClass(_th.attr("class")).html(_th.html()).width(_th.width() + 1)
            .click(function(){
              _th.click();
            })
          ).hide().css({left: $table.data('left'), top: $table.data('top')});
    	}
      } else if (/msie/.test(navigator.userAgent.toLowerCase())) {
        $list.append($("<li></li>")
          .addClass(_th.attr("class")).html(_th.html()).width(_th.width() + 1)
          .click(function(){
            _th.click();
          })
        ).hide().css({left: $table.data('left'), top: $table.data('top')});
      } else {
    	$list.append($("<li></li>").addClass(_th.attr("class")).html(_th.html()).width(_th.width())
    	  .click(function(){
    	    _th.click();
    	  })
    	).hide().css({left: $table.data('left'), top: $table.data('top')});
      }
    });
    $('body').append($list);
    
    $(window).scroll(function() {
      clearTimeout(timer);
      timer = setTimeout(function() {
        if ($table.data('top') < $(document).scrollTop() && $(document).scrollTop() < $table.data('bottom')) {
          $list
            .show()
            .stop()
            .animate({
              top: $(document).scrollTop(),
              opacity: 1
            });
        } else {
          $list.fadeOut(function() {
            $(this).css({top: $table.data('top')});
          });
        }
      }, 100);
    });    
  });
};
