
package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class resourceallocation {

    public static void main(String[] args) {

        int numUsers = 1;
        Calendar calendar = Calendar.getInstance();
        boolean traceFlag = false;

        CloudSim.init(numUsers, calendar, traceFlag);

        createDatacenter("Datacenter_0");

        DatacenterBroker broker = createBroker();
        int brokerId = broker.getId();

        // Create 4 Virtual Machines
        List<Vm> vmList = new ArrayList<Vm>();

        for (int i = 0; i < 4; i++) {

            Vm vm = new Vm(
                    i,
                    brokerId,
                    1000,
                    1,
                    1024,
                    1000,
                    10000,
                    "Xen",
                    new CloudletSchedulerTimeShared()
            );

            vmList.add(vm);
        }

        broker.submitVmList(vmList);

        // Create 8 Cloudlets
        List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

        for (int i = 0; i < 8; i++) {

            Cloudlet cloudlet = new Cloudlet(
                    i,
                    40000,
                    1,
                    300,
                    300,
                    new UtilizationModelFull(),
                    new UtilizationModelFull(),
                    new UtilizationModelFull()
            );

            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
        }

        broker.submitCloudletList(cloudletList);

        // Run the simulation
        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        // Display results
        List<Cloudlet> resultList =
                broker.getCloudletReceivedList();

        printCloudletResults(resultList);
    }

    // Create Datacenter with 4 Hosts
    private static Datacenter createDatacenter(String name) {

        List<Host> hostList = new ArrayList<Host>();

        for (int i = 0; i < 4; i++) {

            int ram = 2048;
            long storage = 1000000;
            int bw = 10000;

            List<Pe> peList = new ArrayList<Pe>();

            peList.add(
                    new Pe(0, new PeProvisionerSimple(1000))
            );

            Host host = new Host(
                    i,
                    new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(bw),
                    storage,
                    peList,
                    new VmSchedulerTimeShared(peList)
            );

            hostList.add(host);
        }

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";

        double timeZone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristics(
                        arch,
                        os,
                        vmm,
                        hostList,
                        timeZone,
                        cost,
                        costPerMem,
                        costPerStorage,
                        costPerBw
                );

        Datacenter datacenter = null;

        try {

            datacenter = new Datacenter(
                    name,
                    characteristics,
                    new VmAllocationPolicySimple(hostList),
                    new LinkedList<Storage>(),
                    0
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    // Create Broker
    private static DatacenterBroker createBroker() {

        DatacenterBroker broker = null;

        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return broker;
    }

    // Print Cloudlet Results
    private static void printCloudletResults(
            List<Cloudlet> list) {

        String indent = "    ";

        System.out.println(
                "Cloudlet ID" + indent +
                "STATUS" + indent +
                "Data center ID" + indent +
                "VM ID" + indent +
                "Time" + indent +
                "Start Time" + indent +
                "Finish Time"
        );

        for (Cloudlet cloudlet : list) {

            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {

                System.out.println(
                        cloudlet.getCloudletId() + indent +
                        "SUCCESS" + indent +
                        cloudlet.getResourceId() + indent +
                        cloudlet.getVmId() + indent +
                        cloudlet.getActualCPUTime() + indent +
                        cloudlet.getExecStartTime() + indent +
                        cloudlet.getFinishTime()
                );
            }
        }
    }
}
