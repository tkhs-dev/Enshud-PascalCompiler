program testBasic;
var i,j,k: integer;

begin
    i := 10;
    j := 20;
    k := i + j * 3 * j div k;
    if k > 100 then
    begin
        k := k - 100;
    end
    else
    begin
        k := k + 100;
    end;
    writeln(k,'AAA');
end.
