/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.data.encounter.service;

import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Base Implementation of the EncounterDataService API
 */
public class EncounterDataServiceImpl extends BaseDefinitionService<EncounterDataDefinition> implements EncounterDataService {

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	@Transactional(readOnly = true)
	public Class<EncounterDataDefinition> getDefinitionType() {
		return EncounterDataDefinition.class;
	}
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 * @should evaluate an encounter data definition
	 */
	@Transactional(readOnly = true)
	public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedEncounterData)super.evaluate(definition, context);
	}
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public EvaluatedEncounterData evaluate(Mapped<? extends EncounterDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedEncounterData)super.evaluate(mappedDefinition, context);
	}
}
