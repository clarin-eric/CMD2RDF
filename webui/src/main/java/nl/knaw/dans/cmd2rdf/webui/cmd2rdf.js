window.onload = setupFunc;

 function setupFunc() {
   document.getElementsByTagName('body')[0].onclick = clickFunc;
   hideBusysign();
   Wicket.Event.subscribe('/ajax/call/beforeSend', function( attributes, jqXHR, settings ) {
	   showBusysign()
	    });
   Wicket.Event.subscribe('/ajax/call/complete', function( attributes, jqXHR, textStatus) {
	   hideBusysign()
	    });
 }

 function hideBusysign() {
   document.getElementById('ajaxveil').style.display ='none';
 }

 function showBusysign() {
   document.getElementById('ajaxveil').style.display ='inline';
 }

 function clickFunc(eventData) {
   var clickedElement = (window.event) ? event.srcElement : eventData.target;
   if ((clickedElement.tagName.toUpperCase() == 'BUTTON' || clickedElement.tagName.toUpperCase() == 'A' || clickedElement.parentNode.tagName.toUpperCase() == 'A'
     || (clickedElement.tagName.toUpperCase() == 'INPUT' && (clickedElement.type.toUpperCase() == 'BUTTON' || clickedElement.type.toUpperCase() == 'SUBMIT'))) 
     && clickedElement.parentNode.id.toUpperCase() != 'NOBUSY' ) {
     showBusysign();
   }
 }
