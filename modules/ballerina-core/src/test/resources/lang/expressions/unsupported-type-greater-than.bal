package lang.expressions;

function checkGreaterThanForUnsupportedType() (boolean){
	json j1;
	json j2;
	j1 = `{"name":"Jack"}`;
	j2 = `{"state":"CA"}`;
	
	return j1 > j2;
}
