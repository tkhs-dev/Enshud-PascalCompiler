program testBasic;
var f: boolean;

begin
    f := true;
    if not f = true then
    begin
        write('then');
    end
    else
    begin
        write('else');
    end;
end.
