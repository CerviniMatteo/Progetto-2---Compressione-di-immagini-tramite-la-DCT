clear; clc; close all;



%% =========================================================
% OUTPUT DIRECTORY
%% =========================================================
cmp_dir = "results";

if ~exist(cmp_dir, 'dir')
    mkdir(cmp_dir);
end

%% =========================================================
% LOOP OVER DATASETS
%% =========================================================

    CSV          =  readtable( ...
    "times_vs_size.csv", ...
    'VariableNamingRule', 'preserve');

    %% =====================================================
    % NORMALIZE COLUMN NAMES
    %% =====================================================
    CSV.Properties.VariableNames = ...
        matlab.lang.makeValidName(CSV.Properties.VariableNames);

    % Display generated names (debug helper)
    disp("Generated variable names:");
    disp(CSV.Properties.VariableNames');
    %% =====================================================
    % RENAME VARIABLES TO SIMPLE NAMES
    %% =====================================================
    rename = {
        'Size',                    'n';
    
        'CustomAvg_s_',           'my_avg';

        'LibraryAvg_s_',          'lib_avg';
    
        'CustomSlowerThanLib___', 'pct_slower';
    };
    for r = 1:size(rename, 1)

        old_name = rename{r,1};
        new_name = rename{r,2};

        idx = strcmp(CSV.Properties.VariableNames, old_name);

        if any(idx)
            CSV.Properties.VariableNames{idx} = new_name;
        end
    end
    %% =====================================================
    % CONVERT CELL COLUMNS TO NUMERIC
    %% =====================================================
    
    vars_to_convert = {
        'n', ...
        'my_avg', 'lib_avg'
    };
    
    for i = 1:length(vars_to_convert)
    
        v = vars_to_convert{i};
    
        if iscell(CSV.(v))
            CSV.(v) = str2double(CSV.(v))/10^9;
        end
    end
    
    %% =====================================================
    % CONVENIENCE VARIABLES
    %% =====================================================
    n       = CSV.n;

    my_avg  = CSV.my_avg;


    lib_avg = CSV.lib_avg;


    %% =====================================================
    % COMMON GRAPH VARIABLES
    %% =====================================================
    x        = (1:numel(n))';
    n_labels = string(n);

    bw = 0.35;

    %% =====================================================
    % 1) SEMILOG PLOT
    %% =====================================================
    f1 = figure;

    semilogy(n, my_avg, '-o', 'LineWidth', 1.5);
    hold on;

    semilogy(n, lib_avg, '-s', 'LineWidth', 1.5);

    xlabel('Matrix size n');
    ylabel('Execution time (s, log-scale)');

    title(['Experimental execution time - ', "benchmark"]);

    legend( ...
        'My DCT-II', ...
        'JTransform DCT-II', ...
        'Location', 'northwest');

    grid on;


    %% =====================================================
    % SAVE FIGURES
    %% =====================================================
    saveas(f1,  fullfile(cmp_dir, ...
        strcat("figure_01_semilogy_", "benchmark", ".png")));



disp("All plots generated successfully.");
