#include "CSI.h"


Complex::Complex(): real(0), imag(0) {}

CSI::CSI(): data(nullptr), num_packets(0), num_channel(0), num_subcarrier(0) {}

CSI::~CSI() {
    if(data) {
        for(int i = 0 ; i < num_packets; i++) {
            delete[] data[i];
        }
        delete[] data;
    }
}

int CSI::packet_length() const {
    return num_channel * num_subcarrier;
}

void CSI::print(std::ostream& os) const {
    for (int i = 0; i < num_packets; i++) {
        for (int j = 0; j < packet_length(); j++) {
            os << data[i][j] << ' ';
        }
        os << std::endl;
    }
}

std::ostream& operator<<(std::ostream &os, const Complex &c) {
    // TODO: problem 1.1

    return os<<c.real<<"+"<<c.imag<<"i";
}

void read_csi(const char *filename,CSI* csi) {
    // TODO: problem 1.2
    std::ifstream openFile(filename);
    if(!openFile.is_open()) return;
    std::string line;
    int count = 0;
    int csi_count;
    int packet_loc;
    int channel_loc;
    int subcarrier_loc;
    while(getline(openFile,line)){
        //std::cout<<line<<std::endl;
        if(count == 0){
            csi->num_packets = std::stoi(line);

        }
        else if(count==1){
            csi->num_channel = std::stoi(line);
        }
        else if(count==2){
            csi->num_subcarrier = std::stoi(line);
            csi->data = new Complex*[csi->num_packets];
            for(int i=0;i<csi->num_packets;i++){
                csi->data[i] = new Complex[csi->packet_length()];
            }
        }
        else if(count%2==1){
            csi_count = (count-3)/2;
            packet_loc = csi_count/(csi->packet_length());
            subcarrier_loc = (csi_count % (csi->packet_length())) / csi->num_channel;
            channel_loc = (csi_count % (csi->packet_length())) % csi->num_channel;
            csi->data[packet_loc][channel_loc * (csi->num_subcarrier) + subcarrier_loc] = Complex();
            csi->data[packet_loc][channel_loc * (csi->num_subcarrier) + subcarrier_loc].real = std::stoi(line);
        }
        else if(count%2==0){
            csi->data[packet_loc][channel_loc * (csi->num_subcarrier) + subcarrier_loc].imag = std::stoi(line);
            //std::cout<<csi->data[packet_loc][channel_loc * (csi->num_subcarrier) + subcarrier_loc]<<std::endl;
            //std::cout<<packet_loc << " " << channel_loc * (csi->num_subcarrier) + subcarrier_loc<<std::endl;
        }
        count++;
    }
//    for(int i=0;i<csi->num_packets;i++){
//        for(int j=0;j<csi->packet_length();j++){
//            std::cout<<csi->data[i][j]<<std::endl;
//        }
//    }
    openFile.close();
}

float** decode_csi(CSI* csi) {
    // TODO: problem 1.3
    float** amplitude_array = new float*[csi->num_packets];

    for(int i=0;i<csi->num_packets;i++){
        amplitude_array[i] = new float[csi->packet_length()];
        for(int j=0;j<csi->packet_length();j++){
            int imag = (csi->data[i][j]).imag;
            int real = (csi->data[i][j]).real;
            amplitude_array[i][j] = sqrt(imag*imag + real*real);
        }
    }
    return amplitude_array;
}

float* get_std(float** decoded_csi, int num_packets, int packet_length) {
    // TODO: problem 1.4
    float* std_array = new float[num_packets];
    for(int i=0;i<num_packets;i++){
        std_array[i] = standard_deviation(decoded_csi[i],packet_length);
    }
    return std_array;
}

void save_std(float* std_arr, int num_packets, const char* filename) {
    // TODO: problem 1.5
    std::ofstream writeFile(filename);
    if(!writeFile.is_open()) return;
    for(int i=0;i<num_packets;i++){
        writeFile << std_arr[i] << " ";
    }

}

// convenience functions
float standard_deviation(float* data, int array_length) {
    float mean = 0, var = 0;
    for (int i = 0; i < array_length; i++) {
        mean += data[i];
    }
    mean /= array_length;
    for (int i = 0; i < array_length; i++) {
        var += pow(data[i]-mean,2);
    }
    var /= array_length;
    return sqrt(var);
}