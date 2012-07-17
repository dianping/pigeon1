[
<#list services as x>
	{
		"name": "${x.name}", 
		"className": "${x.className}",
		"methods": [
		<#list x.methods as m>
			{ 
				"name":"${m.name}",
				"parameterTypes": [
				<#list m.parameterTypes as p>
					"${p.name}"<#if p_has_next>,</#if>
				</#list>
				]
			}<#if m_has_next>,</#if>
		</#list>	
		]	
	}<#if x_has_next>,</#if>
</#list>
]
