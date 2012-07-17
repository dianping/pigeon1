<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link href="/ztree/ztree.css" rel="stylesheet" type="text/css"/>
  <script src="/jquery/jquery-1.7.2.min.js"></script>
  <script src="/ztree/jquery.ztree.core-3.3.min.js"></script>
	<SCRIPT type="text/javascript">
		<!--
		var setting = {
			data: {
				simpleData: {
					enable: true
				}
			}
		};

		var zNodes =[
			<#list services as x>
			{ id:${x_index + 1}, pId:0, name:"${x.name} - ${x.className}", isParent:true}<#if (x.methods?size>0) >,<#elseif x_has_next>,</#if>
				<#list x.methods as m>
			{ id:${x_index + 1}${m_index + 1}, pId:${x_index + 1}, name:"${m.name}(<#list m.parameterTypes as p>${p.name}<#if p_has_next>,</#if></#list>)"}<#if m_has_next>,<#elseif x_has_next>,</#if>
				</#list>
			</#list> 
		];

		$(document).ready(function(){
			$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		});
		//-->
	</SCRIPT>
</head>
<body style="font-size:62.5%;">
<div>
<h1>Pigeon services registered on port ${port?c}:</h1>
</div>
<div>
	<div class="zTreeDemoBackground left">
		<ul id="treeDemo" class="ztree"></ul>
	</div>
</div>
</body>
</html>