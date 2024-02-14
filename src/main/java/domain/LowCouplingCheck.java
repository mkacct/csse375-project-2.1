package domain;

import java.util.Set;

import datasource.Configuration;

public class LowCouplingCheck extends GraphCheck {

    @Override
    public String getName() {
        return "Low Coupling Check";
    }

    @Override
    public Set<Message> gRun(Configuration config) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gRun'");
    }
    
}
