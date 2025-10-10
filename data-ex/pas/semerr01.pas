program testBasic;
var i,j,k: integer;test: boolean;

begin
    i := 1;
    j := 2;
    k := 3;
    test := i = j = k; { should be an error }
end.
