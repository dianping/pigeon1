[
<#list services as x>
	{
		"name": "${x.name}", 
		"type": "${x.type.name}",
		"methods": [
		<#list x.methods as m>
			{ 
				"name":"${m.name}",
				"parameterTypes": [
				<#list m.parameterTypes as p>
					"${p.name}"<#if p_has_next>,</#if>
				</#list>
				],
				"returnType": "${m.returnType.name}"
			}<#if m_has_next>,</#if>
		</#list>	
		]	
	}<#if x_has_next>,</#if>
</#list>
]
