package org.openmrs.module.reporting.web.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.module.util.ReflectionUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MappedPropertyFormController {

	protected static Log log = LogFactory.getLog(MappedPropertyFormController.class);
	
	/**
	 * Default Constructor
	 */
	public MappedPropertyFormController() { }
    
    /**
     * Saves mapped parameters
     */
    @RequestMapping("/module/reporting/reports/saveMappedParameters")
    @SuppressWarnings("unchecked")
    public String saveMappedParameters(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="type") Class<? extends Parameterizable> type,
    		@RequestParam(required=true, value="uuid") String uuid,
            @RequestParam(required=true, value="property") String property,
            @RequestParam(required=false, value="collectionKey") String collectionKey,
            @RequestParam(required=false, value="mappedUuid") String mappedUuid) {
    	
    	Parameterizable parent = ParameterizableUtil.getParameterizable(uuid, type);
    	Field f = ReflectionUtil.getField(type, property);
    	Class<?> fieldType = ReflectionUtil.getFieldType(f);
    	
		Class<? extends Parameterizable> mappedType = null;
		if (StringUtils.isNotEmpty(property)) {
			mappedType = ParameterizableUtil.getMappedType(type, property);
		}
		
		Parameterizable valToSet = null;
		Object previousValue = ReflectionUtil.getPropertyValue(parent, property);
		
		if (StringUtils.isNotEmpty(mappedUuid)) {
			valToSet = ParameterizableUtil.getParameterizable(mappedUuid, mappedType);
    		
    		Mapped m = new Mapped();
    		m.setParameterizable(valToSet);
    		
        	for (Parameter p : valToSet.getParameters()) {
        		String valueType = request.getParameterValues("valueType_"+p.getName())[0];
        		String[] value = request.getParameterValues(valueType+"Value_"+p.getName());
        		if (value != null && value.length > 0) {
    	    		String paramValue = null;
    	    		if (StringUtils.isEmpty(valueType) || valueType.equals("fixed")) {
    	    			paramValue = OpenmrsUtil.join(Arrays.asList(value), ",");
    	    		}
    	    		else {
    	    			paramValue = "${"+value[0]+"}";
    	    		}
    	    		m.addParameterMapping(p.getName(), paramValue);
        		}
        	}
        	
        	if (previousValue != null || valToSet != null) {
        		
        		if (List.class.isAssignableFrom(fieldType)) {
        			List newValue = null;
        			if (previousValue == null) {
        				newValue = new ArrayList();
        				newValue.add(m);
        			}
        			else {
        				newValue = (List)previousValue;
        				if (StringUtils.isEmpty(collectionKey)) {
        					newValue.add(m);
        				}
        				else {
        					int listIndex = Integer.parseInt(collectionKey);
        					newValue.set(listIndex, m);
        				}
        			}
        			ReflectionUtil.setPropertyValue(parent, f, newValue);
        		}
        		else if (Map.class.isAssignableFrom(fieldType)) {
        			Map newValue = (previousValue == null ? new HashMap() : (Map)previousValue);
        			newValue.put(collectionKey, m);
        			ReflectionUtil.setPropertyValue(parent, f, newValue);
        		}
        		else if (Mapped.class.isAssignableFrom(fieldType)) {
        			ReflectionUtil.setPropertyValue(parent, f, m);
        		}
        		else {
        			throw new IllegalArgumentException("Cannot set property fo type: " + fieldType + " to " + m);
        		}
        	}
    	}
    	
    	ParameterizableUtil.saveParameterizable(parent);
    	
    	return "redirect:/module/reporting/closeWindow.htm";
    }
}
