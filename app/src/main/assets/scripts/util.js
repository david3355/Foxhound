
function removeChildNodes(node){
	while (node.firstChild) {
		node.removeChild(node.firstChild);
	}
}

function getParameter(current_url, parameter_name){
	var url = new URL(current_url);
	return url.searchParams.get(parameter_name);
}


function setTitle(title_tag_id, title){
	var title_tag = document.getElementById(title_tag_id);
	title_tag.innerHTML = title;
}

function addDataToTable(row, text, tooltip = "")
{
	tdata = document.createElement('td');
	tdata.appendChild(document.createTextNode(text));
	if (tooltip != ""){
		tdata.setAttribute("data-toggle", "tooltip");
		tdata.setAttribute("title", tooltip);
	}
	row.appendChild(tdata);
}

function getUnitValue(containerId)
{
	var units = document.getElementById(containerId).children;
	for(var i = 0; i < units.length; i++){
		if(units[i].checked){ return units[i].value; }
	}
}